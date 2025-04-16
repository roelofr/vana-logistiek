package dev.roelofr.domain.projections;

import dev.roelofr.domain.User;
import dev.roelofr.domain.Vendor;
import dev.roelofr.domain.enums.TicketStatus;

import java.time.LocalDateTime;

public record TicketSummaryProjection(
    Long id,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime completedAt,
    String description,
    TicketStatus status,
    Vendor vendor,
    User creator,
    int commentCount
) {
}
