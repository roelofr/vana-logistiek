package dev.roelofr.service;

import dev.roelofr.domain.Ticket;
import dev.roelofr.domain.Vendor;
import dev.roelofr.repository.TicketRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TicketService {
    final TicketRepository ticketRepository;

    public Optional<Ticket> findById(long id) {
        return ticketRepository.findByIdOptional(id);
    }

    public List<Ticket> findByVendor(@Nonnull Vendor vendor) {
        return ticketRepository
            .find("#Ticket.ByVendorWithOwner", vendor)
            .list();
    }
}
