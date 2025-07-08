package dev.roelofr.rest.request;

import dev.roelofr.domain.enums.TicketType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Map;

public record TicketCreateRequest(
    @Positive Long vendorId,
    @NotBlank String description,
    @Valid TicketType type,
    Map<String, @NotNull Object> data
) {
}
