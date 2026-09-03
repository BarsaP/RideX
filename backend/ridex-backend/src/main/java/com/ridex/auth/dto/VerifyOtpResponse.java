package com.ridex.auth.dto;

public record VerifyOtpResponse(
        boolean verified,
        String message
) {
}
