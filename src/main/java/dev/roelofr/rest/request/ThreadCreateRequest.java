package dev.roelofr.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ThreadCreateRequest(
    @NotBlank String subject,
    @Positive Long vendorId
) {
    //
}
