package dev.roelofr.integrations.hanko.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
record HankoSession(
    String sessionId,
    String subject,
    Instant issuedAt,
    Instant expiration,
    List<String> audience,
    String issuer,
    HankoEmailAddress email
) {
    public boolean isCurrent() {
        var now = Instant.now();
        return now.isAfter(issuedAt()) && now.isBefore(expiration());
    }

}
