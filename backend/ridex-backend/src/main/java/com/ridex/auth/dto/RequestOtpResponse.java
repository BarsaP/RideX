package com.ridex.auth.dto;

import java.time.Instant;

public record RequestOtpResponse(
       String message,
       Instant expiresAt
) {
}
