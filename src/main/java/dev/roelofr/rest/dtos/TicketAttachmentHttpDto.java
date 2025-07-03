package dev.roelofr.rest.dtos;

import dev.roelofr.domain.TicketAttachment;
import dev.roelofr.domain.enums.AttachmentType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

public record TicketAttachmentHttpDto(Long id,
                                      @Nonnull LocalDateTime createdAt,
                                      @Nullable EmbeddedTicket ticket,
                                      @Nullable EmbeddedUser user,
                                      @Nonnull AttachmentType type,
                                      @Nullable String summary,
                                      @Nullable String description
) {
    public TicketAttachmentHttpDto(TicketAttachment attachment) {
        this(
            attachment.getId(),
            attachment.getCreatedAt(),
            new EmbeddedTicket(attachment.getTicket()),
            new EmbeddedUser(attachment.getUser()),
            attachment.getType(),
            attachment.getSummary(),
            attachment.getDescription()
        );
    }
}
