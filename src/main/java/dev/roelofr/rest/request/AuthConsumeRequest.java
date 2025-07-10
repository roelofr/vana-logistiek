package dev.roelofr.rest.request;

import jakarta.validation.constraints.NotBlank;

public record AuthConsumeRequest(
    @NotBlank String token
) {
}
