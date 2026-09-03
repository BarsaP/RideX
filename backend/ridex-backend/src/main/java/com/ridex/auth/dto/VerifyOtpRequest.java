package com.ridex.auth.dto;

import com.ridex.auth.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record VerifyOtpRequest(

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Phone number must contain exactly 10 digits"
        )
        String phone,

        @NotNull(message = "OTP purpose is required")
        OtpPurpose purpose,

        @NotBlank(message = "OTP is required")
        @Pattern(
                regexp = "^[0-9]{6}$",
                message = "OTP must contain exactly 6 digits"
        )
        String code
) {

}
