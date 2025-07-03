package dev.roelofr.service;

import dev.roelofr.domain.Ticket;
import dev.roelofr.domain.TicketAttachment;
import dev.roelofr.domain.User;
import dev.roelofr.domain.Vendor;
import dev.roelofr.domain.enums.AttachmentType;
import dev.roelofr.domain.enums.TicketStatus;
import dev.roelofr.domain.enums.TicketType;
import dev.roelofr.repository.TicketAttachmentRepository;
import dev.roelofr.repository.TicketRepository;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TicketAttachmentRepository ticketAttachmentRepository;

    public Optional<Ticket> findById(long id) {
        return ticketRepository.findByIdOptional(id);
    }

    public List<Ticket> findByVendor(@Nonnull Vendor vendor) {
        return ticketRepository
            .find("#Ticket.ByVendorWithOwner", vendor)
            .list();
    }

    @Transactional
    public Ticket createTicket(@Nonnull User user, @Nonnull Vendor vendor, @Nonnull TicketType type, String description) {
        var ticket = Ticket.builder()
            .vendor(vendor)
            .creator(user)
            .status(TicketStatus.Created)
            .description(description)
            .build();

        ticketRepository.persist(ticket);

        var attachment = TicketAttachment.builder()
            .ticket(ticket)
            .user(user)
            .type(AttachmentType.Created)
            .build();

        ticketAttachmentRepository.persist(attachment);

        return ticket;
    }
}
