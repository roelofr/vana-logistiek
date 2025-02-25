package dev.roelofr.rest.dtos;

import dev.roelofr.domain.Ticket;
import dev.roelofr.domain.enums.TicketStatus;

public record TicketHttpDto(
    long id,
    TicketStatus status,
    String description,
    EmbeddedVendor vendor,
    EmbeddedUser creator,
    EmbeddedDistrict district
) {
    public TicketHttpDto(Ticket ticket) {
        this(
            ticket.getId(),
            ticket.getStatus(),
            ticket.getDescription(),
            new EmbeddedVendor(ticket.getVendor()),
            new EmbeddedUser(ticket.getCreator()),
            new EmbeddedDistrict(ticket.getVendor().getDistrict())
        );
    }
}
