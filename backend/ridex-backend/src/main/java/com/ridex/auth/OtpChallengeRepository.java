package com.ridex.auth;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, UUID> {

    Optional<OtpChallenge> findTopByPhoneAndPurposeOrderByCreatedAtDesc(
            String phone,
            OtpPurpose purpose
    );

    Optional<OtpChallenge> findTopByPhoneAndPurposeAndVerifiedAtIsNullAndInvalidatedAtIsNullOrderByCreatedAtDesc(
            String phone,
            OtpPurpose purpose
    );

    @Modifying
    @Transactional
    @Query("""
           UPDATE OtpChallenge otp
           SET otp.invalidatedAt = :now
           WHERE otp.phone = :phone
              AND otp.purpose = :purpose
              AND otp.verifiedAt IS NULL
              AND otp.invalidatedAt IS NULL
           """)
    void invalidatedActiveOtps(
            @Param("phone") String phone,
            @Param("purpose") OtpPurpose purpose,
            @Param("now")Instant now
            );

}
