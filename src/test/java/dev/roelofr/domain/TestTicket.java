package dev.roelofr.domain;

import dev.roelofr.domain.enums.TicketStatus;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class TestTicket extends Ticket {
    private static final AtomicLong incremental = new AtomicLong(0L);

    public static TestTicket make(Long id, String description, @Nullable Vendor vendor) {
        TestTicket ticket = new TestTicket();
        ticket.setId(id);

        ticket.setVendor(vendor);
        ticket.setDescription(description);
        ticket.setStatus(TicketStatus.Created);

        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setCreator(TestUser.make("Test user"));

        return ticket;
    }

    public static TestTicket make(String description, @Nullable Vendor vendor) {
        return make(incremental.incrementAndGet(), description, vendor);
    }

    public static TestTicket make(String description) {
        return make(incremental.incrementAndGet(), description, null);
    }

    private void setId(Long id) {
        this.id = id;
    }

    public TestTicket updated() {
        this.setStatus(TicketStatus.Updated);
        return this;
    }

    public TestTicket assigned() {
        this.setStatus(TicketStatus.Assigned);
        return this;
    }

    public TestTicket completed() {
        this.setStatus(TicketStatus.Resolved);
        this.setCompletedAt(LocalDateTime.now());
        return this;
    }
}
