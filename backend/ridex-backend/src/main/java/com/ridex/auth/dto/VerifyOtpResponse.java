package com.ridex.auth.dto;

import com.ridex.auth.jwt.dto.LoginResponse;

public record VerifyOtpResponse(
        boolean verified,
        String message,
        LoginResponse tokens
) {
}
