package dev.roelofr.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.roelofr.domain.enums.AttachmentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ticket_attachments")
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NamedQueries({
    @NamedQuery(
        name = "TicketAttachment.findForUser",
        query = """
            SELECT attachment
            FROM TicketAttachment AS attachment
            JOIN FETCH Ticket AS ticket
            WHERE attachment.user = ?1
            ORDER BY attachment.createdAt
            """
    )
})
public class TicketAttachment extends Model {
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @ManyToOne
    @ToString.Exclude
    @JsonBackReference
    @JoinColumn(name = "ticket_id")
    Ticket ticket;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    User user;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    AttachmentType type;

    @Column(name = "summary", length = 200)
    String summary;

    @Column(name = "description", columnDefinition = "text")
    String description;

    @ToString.Include(name = "ticket")
    Long getTicketForToString() {
        return ticket == null ? null : ticket.getId();
    }

    @ToString.Include(name = "user")
    Long getUserForToString() {
        return user == null ? null : user.getId();
    }
}
