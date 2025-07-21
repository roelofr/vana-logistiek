package dev.roelofr.rest.request;

import jakarta.validation.constraints.NotBlank;

public record SetUserNameRequest(
    @NotBlank String name
) {
}
