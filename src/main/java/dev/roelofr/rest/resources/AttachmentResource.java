package dev.roelofr.rest.resources;

import dev.roelofr.config.Roles;
import dev.roelofr.repository.TicketAttachmentRepository;
import dev.roelofr.repository.TicketRepository;
import dev.roelofr.rest.dtos.TicketAttachmentHttpDto;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RolesAllowed(Roles.User)
@RequiredArgsConstructor
@Path("/ticket/{ticketId}/attachment")
public class AttachmentResource {
    private final TicketRepository ticketRepository;
    private final TicketAttachmentRepository ticketAttachmentRepository;

    @PathParam("ticketId")
    private Long ticketId;

    private Ticket getTicket() {
        if (ticketId == null)
            throw new NotFoundException();

        var ticket = ticketRepository.findByIdOptional(ticketId);
        if (ticket.isEmpty())
            throw new NotFoundException("Ticket with id [%d] was not found".formatted(ticketId));

        return ticket.get();
    }

    @GET
    @Path("/")
    public List<TicketAttachmentHttpDto> getAttachments() {
        var ticket = getTicket();

        return ticketAttachmentRepository.findForTicket(ticket)
            .stream().map(TicketAttachmentHttpDto::new)
            .toList();
    }
}
