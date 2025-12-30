package dev.roelofr.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Map;

public record TicketCreateRequest(
    @Positive Long vendorId,
    @NotBlank String description,
    Map<String, @NotNull Object> data
) {
}
