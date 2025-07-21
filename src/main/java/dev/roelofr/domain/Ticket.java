package dev.roelofr.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.roelofr.domain.enums.TicketStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

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
        """),
    @NamedQuery(name = "Ticket.ByUserWithOwner", query = """
        SELECT ticket
        FROM Ticket as ticket
        LEFT JOIN FETCH ticket.creator
        WHERE ticket.creator = ?1 OR ticket.assignee = ?1
        """),
    @NamedQuery(name = "Ticket.ListWithCommentCount", query = """
            SELECT ticket, COUNT(attachment) AS comment_count
            FROM Ticket AS ticket
            LEFT JOIN TicketAttachment AS attachment
            ON attachment.ticket = ticket
            GROUP BY ticket.id
        """)
})
public class Ticket extends Model {
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @Builder.Default
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "completed_at")
    LocalDateTime completedAt = null;

    @Column(length = 100)
    String description;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar", length = 10)
    TicketStatus status = TicketStatus.Created;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "vendor_id")
    Vendor vendor;

    @ManyToOne()
    @JsonManagedReference
    @JoinColumn(name = "creator_id", nullable = false)
    User creator;

    @ManyToOne()
    @JsonManagedReference
    @JoinColumn(name = "assignee_id")
    User assignee;


    @JsonManagedReference
    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY)
    List<TicketAttachment> attachments;
}
