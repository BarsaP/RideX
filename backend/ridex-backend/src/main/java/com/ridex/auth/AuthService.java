package com.ridex.auth;

import com.ridex.auth.dto.RequestOtpRequest;
import com.ridex.auth.dto.RequestOtpResponse;
import com.ridex.auth.dto.VerifyOtpRequest;
import com.ridex.auth.dto.VerifyOtpResponse;
import com.ridex.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
public class AuthService {

    private static final Duration OTP_EXPIRY = Duration.ofMinutes(5);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
    private static final int MAX_ATTEMPTS = 5;

    private final OtpChallengeRepository otpChallengeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            OtpChallengeRepository otpChallengeRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ){
        this.otpChallengeRepository = otpChallengeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RequestOtpResponse requestOtp(RequestOtpRequest request){
        validatePurpose(request.phone(), request.purpose());

        Instant now = Instant.now();

        otpChallengeRepository.findTopByPhoneAndPurposeOrderByCreatedAtDesc(
                request.phone(),
                request.purpose()
        ).ifPresent(latest -> {
            if(latest.getCreatedAt().plus(RESEND_COOLDOWN).isAfter(now)){
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Please wait before requesting another OTP"
                );
            }
        });

        otpChallengeRepository.invalidatedActiveOtps(
                request.phone(),
                request.purpose(),
                now
        );

        String rawOtp = generatedOtp();

        OtpChallenge challenge = new OtpChallenge();
        challenge.setPhone(request.phone());
        challenge.setPurpose(request.purpose());
        challenge.setCodeHash(passwordEncoder.encode(rawOtp));
        challenge.setExpiresAt(now.plus(OTP_EXPIRY));

        otpChallengeRepository.save(challenge);

        // Local development only. Replace this with an SMS-provider adapter in production.
        log.warn(
                "LOCAL DEVELOPMENT OTP - phone: {}, purpose: {}, code: {}",
                request.phone(),
                request.purpose(),
                rawOtp
        );

        return new RequestOtpResponse(
                "OTP sent successfully",
                challenge.getExpiresAt()
        );
    }

    @Transactional
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request){
        OtpChallenge challenge = otpChallengeRepository
                .findTopByPhoneAndPurposeAndVerifiedAtIsNullAndInvalidatedAtIsNullOrderByCreatedAtDesc(
                        request.phone(),
                        request.purpose()
                )

                .orElseThrow(() -> invalidOtp());

        Instant now = Instant.now();

        if(!challenge.getExpiresAt().isAfter(now)){
            throw invalidOtp();
        }

        if(challenge.getAttemptCount() >= MAX_ATTEMPTS){
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Maximum OTP attempts exceeded. Request a new OTP."
            );
        }

        challenge.setAttemptCount(challenge.getAttemptCount() + 1);

        if(!passwordEncoder.matches(request.code(), challenge.getCodeHash())){
            otpChallengeRepository.save(challenge);
            throw invalidOtp();
        }

        challenge.setVerifiedAt(now);
        otpChallengeRepository.save(challenge);

        return new VerifyOtpResponse(true, "OTP verified successfully");
    }

    private void validatePurpose(String phone, OtpPurpose purpose){
        if(purpose == OtpPurpose.LOGIN && !userRepository.existsByPhone(phone)){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No account exists for this phone number"
            );
        }

        if(purpose == OtpPurpose.REGISTRATION && userRepository.existsByPhone(phone)){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account already exists for this phone number"
            );
        }
    }

    private String generatedOtp(){
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    private ResponseStatusException invalidOtp(){
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid or expired OTP"
        );
    }
    
}
