package dev.roelofr.domain;

import dev.roelofr.domain.enums.TicketStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "tickets")
public class Ticket extends Model {
    @Setter(AccessLevel.NONE)
    @Column(name = "ticket_number", nullable = true)
    String ticketNumber;

    String description;

    @Enumerated(EnumType.STRING)
    TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    Vendor vendor;

    @ManyToOne()
    @JoinColumn(name = "creator_id")
    User creator;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    LocalDateTime completedAt = null;

    @PrePersist
    void updateTimestampsOnCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
}
