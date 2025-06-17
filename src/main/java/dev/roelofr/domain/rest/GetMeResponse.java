package dev.roelofr.domain.rest;

import lombok.Builder;

@Builder
public record GetMeResponse(
    String name,
    String email
) {
    //
}
