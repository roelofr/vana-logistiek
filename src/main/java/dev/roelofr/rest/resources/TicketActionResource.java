package dev.roelofr.rest.resources;

import dev.roelofr.config.Roles;
import dev.roelofr.domain.Ticket;
import dev.roelofr.domain.enums.AttachmentType;
import dev.roelofr.domain.enums.TicketStatus;
import dev.roelofr.rest.request.TicketAssignRequest;
import dev.roelofr.rest.request.TicketCommentRequest;
import dev.roelofr.rest.request.TicketResolveRequest;
import dev.roelofr.rest.request.TicketStatusRequest;
import dev.roelofr.service.TicketAttachmentService;
import dev.roelofr.service.TicketService;
import dev.roelofr.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestPath;

import java.time.LocalDateTime;

@Slf4j
@RequestScoped
@RequiredArgsConstructor
@RolesAllowed(Roles.User)
@Path("/ticket/{id}/do")
public class TicketActionResource {
    final TicketAttachmentService attachmentService;
    final TicketService ticketService;
    final UserService userService;

    @RestPath("id")
    Long ticketId;

    Ticket getTicket() {
        return ticketService.findById(ticketId).orElse(null);
    }

    @POST
    @Path("/comment")
    @Transactional
    public Response comment(@Valid TicketCommentRequest commentRequest) {
        final var ticket = getTicket();
        if (ticket == null)
            return Response.status(Status.NOT_FOUND).build();

        var attachment = attachmentService.create(ticket, AttachmentType.Comment);
        attachment.setDescription(commentRequest.comment());

        return Response.ok(ticket).build();
    }

    @POST
    @Transactional
    @Path("/status")
    @RolesAllowed({Roles.CentralePost})
    public Response status(@Valid TicketStatusRequest request) {
        final var ticket = getTicket();
        if (ticket == null)
            return Response.status(Status.NOT_FOUND).build();

        final var status = request.status();

        if (status == TicketStatus.Created)
            throw new BadRequestException("Ticket cannot be reverted to that state");

        ticket.setStatus(status);

        if (status == TicketStatus.Resolved)
            ticket.setCompletedAt(LocalDateTime.now());

        var attachment = attachmentService.create(ticket, AttachmentType.StatusChange, status.name());
        attachment.setDescription(status.name());

        return Response.ok(ticket).build();
    }

    @POST
    @Path("/assign")
    @Transactional
    public Response assign(@Valid TicketAssignRequest assignRequest) {
        final var ticket = getTicket();
        if (ticket == null)
            return Response.status(Status.NOT_FOUND).build();

        var assigneeOptional = userService.findById(assignRequest.userId());
        if (assigneeOptional.isEmpty())
            return Response.status(Status.BAD_REQUEST).build();

        if (ticket.getStatus().equals(TicketStatus.Resolved))
            return Response.status(Status.CONFLICT).build();

        ticketService.assignTo(ticket, assigneeOptional.get(), assignRequest.comment());

        return Response.ok(ticket).build();
    }

    @POST
    @Transactional
    @Path("/resolve")
    public Response resolve(@Valid @NotNull TicketResolveRequest resolveRequest) {
        final var ticket = getTicket();
        if (ticket == null)
            return Response.status(Status.NOT_FOUND).build();

        if (ticket.getStatus().equals(TicketStatus.Resolved))
            return Response.status(Status.CONFLICT).build();

        ticketService.resolve(ticket, resolveRequest.comment());

        return Response.ok(ticket).build();
    }
}
