package dev.roelofr.domain;

import dev.roelofr.domain.enums.TicketStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tickets")
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NamedQueries({
    @NamedQuery(name = "Ticket.ByVendorWithOwner", query = """
    SELECT ticket
    FROM Ticket as ticket
    LEFT JOIN FETCH ticket.creator
    WHERE ticket.vendor = ?1
    """)
})
public class Ticket extends Model {
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "completed_at")
    LocalDateTime completedAt = null;

    @Column(length = 100)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar", length = 10)
    TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    Vendor vendor;

    @ManyToOne()
    @JoinColumn(name = "creator_id")
    User creator;

    @PrePersist
    void updateTimestampsOnCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
}
