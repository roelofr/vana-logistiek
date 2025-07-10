package dev.roelofr.integrations.hanko.model;

import java.time.Instant;

public record SessionValidationResponse(
    boolean isValid,
    Instant expirationTime,
    String userId,
    HankoSession claims
) {
    public boolean isExpired() {
        return Instant.now().isAfter(expirationTime());
    }
}
