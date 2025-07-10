package dev.roelofr.domain.rest;

import lombok.Builder;

import java.util.List;

@Builder
public record AuthConsumeResponse(
    String name,
    String email,
    List<String> roles,
    String jwt
) {
    //
}
