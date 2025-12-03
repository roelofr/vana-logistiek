package dev.roelofr.domain;


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

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "threads")
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NamedQueries({
    @NamedQuery(
        name = "sortedByVendor",
        query = "select t from threads t where t.vendor = ? order by t.updatedAt desc"
    )
})
public class Thread extends Model {
    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    Team team;

    @ManyToOne
    @JoinColumn(name = "assigned_team_id")
    Team assignedTeam;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    User assignedUser;

    @Column(name = "subject", length = 200)
    String subject;

    @Builder.Default
    @Column(name = "read")
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
