package dev.roelofr.rest.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record VendorUpdateRequest(
    @Nullable @NotBlank String name,
    @Nullable @NotBlank String type,
    @Nullable @NotBlank String district
) {
}
