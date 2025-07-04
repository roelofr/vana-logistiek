package dev.roelofr.domain.rest;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record PostLoginResponse(
    String name,
    String email,
    List<String> roles,
    String jwt,
    OffsetDateTime expiration
) {
    //
}
