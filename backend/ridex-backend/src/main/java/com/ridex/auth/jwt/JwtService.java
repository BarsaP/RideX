package com.ridex.auth.jwt;

import com.ridex.user.User;
import com.ridex.user.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessTokenTtlSeconds;

    public JwtService(
            @Value("${jwt.secret}") String base64Secret,
            @Value("${jwt.access-token-ttl-seconds}") long accessTokenTtlSeconds
    ){
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
    }

    public String generateAccessToken(User user){
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenTtlSeconds)))
                .signWith(signingKey)
                .compact();
    }

    public JwtPrincipal parseAccessToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new JwtPrincipal(
                UUID.fromString(claims.getSubject()),
                UserRole.valueOf(claims.get("role", String.class))
        );
    }

    public long getAccessTokenTtlSeconds(){
        return accessTokenTtlSeconds;
    }
}
