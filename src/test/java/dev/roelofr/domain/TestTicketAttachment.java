package dev.roelofr.domain;

import dev.roelofr.domain.enums.AttachmentType;

import java.util.concurrent.atomic.AtomicLong;

public class TestTicketAttachment extends TicketAttachment {
    private static final AtomicLong incremental = new AtomicLong(0L);

    public static TicketAttachment make(Long id, Ticket ticket, AttachmentType type, User user, String description) {
        var attachment = new TestTicketAttachment();

        attachment.setId(id);
        attachment.setTicket(ticket);
        attachment.setType(type);
        attachment.setUser(user);
        attachment.setDescription(description);

        return attachment;
    }

    public static TicketAttachment make(Ticket ticket, AttachmentType type, User user, String description) {
        return make(incremental.incrementAndGet(), ticket, type, user, description);
    }


    public static TicketAttachment make(Ticket ticket, User user) {
        return make(ticket, AttachmentType.Comment, user, "Random Comment");
    }

    public static TicketAttachment make(Ticket ticket) {
        return make(ticket, TestUser.make("Steve"));
    }

    private void setId(Long id) {
        this.id = id;
    }
}
