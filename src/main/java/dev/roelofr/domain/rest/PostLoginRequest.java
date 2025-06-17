package dev.roelofr.domain.rest;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PostLoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {
    //
}
