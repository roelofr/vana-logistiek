package dev.roelofr.domain;


import dev.roelofr.domain.enums.UpdateType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Table(name = "thread_updates")
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorColumn(name = "type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class ThreadUpdate extends Model {
    @ManyToOne
    @JoinColumn(name = "thread_id", nullable = false)
    Thread thread;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    Team team;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    UpdateType type;

    @Column(name = "created_at")
    Instant createdAt;

    @PrePersist
    void setCreationTimestamps() {
        createdAt = Instant.now();
    }

    public class ThreadMessage extends ThreadUpdate {
        @Column(name = "data.message")
        String message;
    }

    public class ThreadCreated extends ThreadUpdate {
        //
    }

    public class ThreadResolved extends ThreadUpdate {
        //
    }

    public class ThreadAssignToTeam extends ThreadUpdate {
        @ManyToOne
        @JoinColumn(name = "data.assigned_to_team_id")
        Team assignedToTeam;

    }

    public class ThreadClaimedByUser extends ThreadUpdate {
        @ManyToOne
        @JoinColumn(name = "data.assigned_to_user_id")
        User assignedToUser;
    }
}
