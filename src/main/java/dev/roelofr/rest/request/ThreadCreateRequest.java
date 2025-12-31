package dev.roelofr.rest.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ThreadCreateRequest(
    @Positive Long vendorId,
    @NotBlank String subject,
    @Nullable String message
) {
    //
}
