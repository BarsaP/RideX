package com.ridex.auth.jwt;

import com.ridex.auth.jwt.dto.LoginResponse;
import com.ridex.user.User;
import com.ridex.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long refreshTokenTtlDays;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${jwt.refresh-token-ttl-days}") long refreshTokenTtlDays
    ){
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenTtlDays = refreshTokenTtlDays;
    }

    @Transactional
    public LoginResponse issueTokens(User user){
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String secret = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setTokenHash(passwordEncoder.encode(secret));
        refreshToken.setExpiresAt(
                Instant.now().plus(refreshTokenTtlDays, ChronoUnit.DAYS)
        );

        refreshTokenRepository.save(refreshToken);

        String rawRefreshToken = refreshToken.getId() + "." + secret;

        return new LoginResponse(
                jwtService.generateAccessToken(user),
                rawRefreshToken,
                "Bearer",
                jwtService.getAccessTokenTtlSeconds()
        );
    }

    @Transactional
    public LoginResponse refresh(String rawRefreshToken){
        RefreshToken storedToken = validateRefreshToken(rawRefreshToken);

        storedToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(storedToken);

        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> invalidRefreshToken());
        return issueTokens(user);
    }

    @Transactional
    public void logout(String rawRefreshToken){
        RefreshToken storedToken = validateRefreshToken(rawRefreshToken);
        storedToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(storedToken);
    }

    private RefreshToken validateRefreshToken(String rawRefreshToken){
        String[] parts = rawRefreshToken.split("\\.", 2);

        if(parts.length != 2){
            throw invalidRefreshToken();
        }

        UUID tokenId;
        try{
            tokenId = UUID.fromString(parts[0]);
        } catch (IllegalArgumentException exception){
            throw invalidRefreshToken();
        }

        RefreshToken token = refreshTokenRepository.findById(tokenId)
                .orElseThrow(this::invalidRefreshToken);

        if(token.getRevokedAt() != null
               || !token.getExpiresAt().isAfter(Instant.now())
               || !passwordEncoder.matches(parts[1], token.getTokenHash())
        ){
            throw invalidRefreshToken();
        }

        return token;
    }

    private ResponseStatusException invalidRefreshToken(){
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid or expired refresh token"
        );
    }
}
