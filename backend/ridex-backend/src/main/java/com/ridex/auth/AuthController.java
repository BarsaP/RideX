package com.ridex.auth;

import com.ridex.auth.dto.RequestOtpRequest;
import com.ridex.auth.dto.RequestOtpResponse;
import com.ridex.auth.dto.VerifyOtpRequest;
import com.ridex.auth.dto.VerifyOtpResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/otp")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/request")
    public ResponseEntity<RequestOtpResponse> requestOtp(
            @Valid @RequestBody RequestOtpRequest request
            ){
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(authService.requestOtp(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
            ){
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

}
