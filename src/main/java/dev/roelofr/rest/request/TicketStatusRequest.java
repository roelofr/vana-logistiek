package dev.roelofr.rest.request;

import dev.roelofr.domain.enums.TicketStatus;
import jakarta.validation.Valid;

public record TicketStatusRequest(
    @Valid TicketStatus status
) {
}
