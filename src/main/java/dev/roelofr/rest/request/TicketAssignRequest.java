package dev.roelofr.rest.request;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

public record TicketAssignRequest(
    @Valid @Positive Long userId,
    @Nullable String comment
) {
    //
}
