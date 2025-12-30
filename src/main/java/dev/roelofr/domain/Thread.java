package dev.roelofr.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Data
@Entity
@Table(name = "threads")
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NamedQueries({
    @NamedQuery(
        name = "Thread.sortedByVendor",
        query = """
            SELECT t
            FROM Thread t
            WHERE t.vendor = ?1
            ORDER BY t.updatedAt desc, t.id asc
            """
    ),
    @NamedQuery(
        name = "Thread.unresolvedSorted",
        query = """
            SELECT t
            FROM Thread t
            WHERE t.resolvedAt IS NULL
            ORDER BY t.updatedAt desc, t.id asc
            """
    ),
    @NamedQuery(
        name = "Thread.allSorted",
        query = """
            SELECT t
            FROM Thread t
            ORDER BY t.updatedAt desc, t.id asc
            """
    ),
    @NamedQuery(
        name = "Thread.findByIdWithAllRelations",
        query = """
            SELECT t
            FROM Thread t
            LEFT JOIN FETCH t.vendor as v
            LEFT JOIN FETCH v.district
            LEFT JOIN FETCH t.user
            LEFT JOIN FETCH t.team
            LEFT JOIN FETCH t.assignedTeam
            LEFT JOIN FETCH t.assignedUser
            WHERE t.id = ?1
            """
    )
})
public class Thread extends Model {
    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"team"})
    User user;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    Team team;

    @ManyToOne
    @JoinColumn(name = "assigned_team_id")
    Team assignedTeam;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    @JsonIgnoreProperties({"team"})
    User assignedUser;

    @Column(name = "subject", length = 200)
    String subject;

    @Builder.Default
    @Column(name = "is_read")
    boolean read = false;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    LocalDateTime resolvedAt;

    @PrePersist
    void setCreationTimestamps() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void setUpdateTimestamps() {
        updatedAt = LocalDateTime.now();
    }
}
