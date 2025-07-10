package dev.roelofr.integrations.hanko.model;

import java.time.Instant;

public record HankoUser(
    String id,
    String email,
    Instant createdAt,
    Instant updatedAt,
    String username
) {
    //
}
