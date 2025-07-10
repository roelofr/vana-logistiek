package dev.roelofr.integrations.hanko.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
