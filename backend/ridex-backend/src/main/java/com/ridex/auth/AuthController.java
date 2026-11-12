package com.ridex.auth;

import com.ridex.auth.dto.RequestOtpRequest;
import com.ridex.auth.dto.RequestOtpResponse;
import com.ridex.auth.dto.VerifyOtpRequest;
import com.ridex.auth.dto.VerifyOtpResponse;
import com.ridex.auth.jwt.RefreshTokenService;
import com.ridex.auth.jwt.dto.LoginResponse;
import com.ridex.auth.jwt.dto.RefreshTokenRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService){

        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/otp/request")
    public ResponseEntity<RequestOtpResponse> requestOtp(
            @Valid @RequestBody RequestOtpRequest request
            ){
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(authService.requestOtp(request));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
            ){
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
            ){
        return ResponseEntity.ok(
                refreshTokenService.refresh(request.refreshToken())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ){
        refreshTokenService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(
            @AuthenticationPrincipal UUID userId
    ){
        return ResponseEntity.ok("Authenticated user ID: " + userId);
    }

}
