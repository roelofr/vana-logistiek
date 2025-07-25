package dev.roelofr.service;

import dev.roelofr.domain.Ticket;
import dev.roelofr.domain.TicketAttachment;
import dev.roelofr.domain.User;
import dev.roelofr.domain.enums.AttachmentType;
import dev.roelofr.repository.TicketAttachmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;

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

        // Check relation
        Hibernate.initialize(ticket.getAttachments());

        log.info("Persisting attachment for {} of type {}", ticket.getId(), type);

        ticketAttachmentRepository.persist(attachment);

        return attachment;
    }

    public TicketAttachment create(Ticket ticket, AttachmentType type) {
        return create(ticket, type, buildSummary(ticket, type));
    }

    private String buildSummary(Ticket ticket, AttachmentType type) {
        var user = getUser();
        return switch (type) {
            case Created -> String.format("Ticket aangemaakt door %s", user.getName());
            case Comment -> String.format("Opmerking geplaatst door %s", user.getName());
            case Assignment ->
                String.format("Ticket toegewezen aan %s door %s", ticket.getAssignee().getName(), user.getName());
            case StatusChange ->
                String.format("Ticket status veranderd naar %s door %s", ticket.getStatus().name(), user.getName());
            default -> "???";
        };
    }
}
