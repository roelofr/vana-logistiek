
package dev.roelofr.domain.rest;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record PostLoginResponse(
    String name,
    String jwt,
    OffsetDateTime expiration
) {
    //
}
