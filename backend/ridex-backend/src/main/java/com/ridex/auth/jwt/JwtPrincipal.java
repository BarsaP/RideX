package com.ridex.auth.jwt;

import com.ridex.user.UserRole;

import java.util.UUID;

public record JwtPrincipal (
    UUID userId,
    UserRole role
) {
}
