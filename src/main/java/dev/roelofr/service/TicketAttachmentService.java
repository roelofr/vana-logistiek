package dev.roelofr.service;

import dev.roelofr.domain.Ticket;
import dev.roelofr.domain.TicketAttachment;
import dev.roelofr.domain.User;
import dev.roelofr.domain.enums.AttachmentType;
import dev.roelofr.repository.TicketAttachmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TicketAttachmentService {
    private final TicketAttachmentRepository ticketAttachmentRepository;

    private final AuthenticationService authenticationService;

    public User getUser() {
        return authenticationService.getCurrentUser().orElseThrow();
    }

    public TicketAttachment create(Ticket ticket, AttachmentType type, String summary) {
        var attachment = TicketAttachment.builder()
            .ticket(ticket)
            .type(type)
            .summary(summary)
            .user(getUser())
            .build();

        ticketAttachmentRepository.persist(attachment);

        return attachment;
    }

    public TicketAttachment create(Ticket ticket, AttachmentType type) {
        return create(ticket, type, buildSummary(ticket, type));
    }

    private String buildSummary(Ticket ticket, AttachmentType type) {
        var user = getUser();
        return switch (type) {
            case Created -> String.format("Ticket %s was created by %s", ticket.getId(), user.getName());
            case Comment -> String.format("Ticket %s was commented on by %s", ticket.getId(), user.getName());
            case Assignment ->
                String.format("Ticket %s was assigned to %s by %s", ticket.getId(), ticket.getAssignee().getName(), user.getName());
            case StatusChange ->
                String.format("Ticket %s was changed to %s by %s", ticket.getId(), ticket.getStatus(), user.getName());
            default -> String.format("Unknown action, performed by %s", user.getName());
        };
    }
}
