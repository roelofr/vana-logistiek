package dev.roelofr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@Table(name = "ticket_metrics")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class TicketMetric extends Model {
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "plot_at")
    private LocalDateTime plotAt;

    @Column(name = "open_count")
    private Long openCount;

    @Column(name = "total_count")
    private Long totalCount;

    @Column(name = "created_count")
    private Long createdCount;

    @Column(name = "updated_count")
    private Long updatedCount;

    @Column(name = "resolved_count")
    private Long resolvedCount;
}
