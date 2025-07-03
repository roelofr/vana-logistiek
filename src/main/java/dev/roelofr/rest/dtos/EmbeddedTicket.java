package dev.roelofr.rest.dtos;

import dev.roelofr.domain.Ticket;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public record EmbeddedTicket(
    long id,
    String description
) {
    public EmbeddedTicket(@Nonnull Ticket ticket) {
        this(
            ticket.getId(),
            ticket.getDescription()
        );
    }

    public static EmbeddedTicket fromNullable(@Nullable Ticket ticket) {
        if (ticket == null)
            return null;

        return new EmbeddedTicket(ticket);
    }
}
