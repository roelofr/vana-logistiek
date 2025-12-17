package dev.roelofr.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ThreadCreateRequest(
    @NotBlank String title,
    @Positive Long vendorId
) {
    //
}
