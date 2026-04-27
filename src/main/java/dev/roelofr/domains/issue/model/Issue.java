package dev.roelofr.domains.issue.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.roelofr.domain.Model;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.vendor.model.Vendor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "issue")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NamedQueries({
    @NamedQuery(
        name = "Issue.findByIdWithAllRelations",
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
    ),
    @NamedQuery(
        name = "Issue.findAllSorted",
        query = """
            SELECT issue FROM Issue issue
            ORDER BY
                CASE WHEN issue.resolvedAt IS NULL THEN 0 ELSE 1 END ASC,
                issue.updatedAt DESC
            """
    ),
    @NamedQuery(
        name = "Issue.findForUserSorted",
        query = """
            SELECT DISTINCT i
            FROM Issue i
            JOIN i.participants p
            WHERE
                -- Condition 1: User is a direct participant
                p.user = :user
                OR
                -- Condition 2: User belongs to a group that is a participant
                EXISTS (
                    SELECT 1
                    FROM p.group g
                    JOIN g.users gu
                    WHERE gu = :user
                )
            ORDER BY
                CASE WHEN i.resolvedAt IS NULL THEN 0 ELSE 1 END ASC,
                i.updatedAt DESC
            """
    )
})
public class Issue extends Model {
    @Column(name = "subject", length = 200)
    String subject;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"team", "group"})
    User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    Group group;

    @ManyToOne
    @JoinColumn(name = "assigned_group_id")
    Group assignedGroup;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    @JsonIgnoreProperties({"team", "group"})
    User assignedUser;

    /**
     * List of participants (users or groups) associated with this issue.
     * Uses mappedBy to link to the IssueParticipant entity.
     */
    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<IssueParticipant> participants = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    LocalDateTime resolvedAt;

}
