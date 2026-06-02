package dev.roelofr.domains.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateVendorRequest(
    @NotBlank String name,
    @NotBlank String number,
    @NotNull @Positive long districtId
) {
}
