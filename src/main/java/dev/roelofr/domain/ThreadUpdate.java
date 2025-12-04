package dev.roelofr.domain;


import dev.roelofr.domain.enums.UpdateType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "thread_updates")
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

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @PrePersist
    void setCreationTimestamps() {
        createdAt = LocalDateTime.now();
    }

    @Entity
    @DiscriminatorValue(UpdateType.Message)
    public static class ThreadMessage extends ThreadUpdate {
        @Column(name = "data.message")
        String message;
    }

    @Entity
    @DiscriminatorValue(UpdateType.Created)
    public static class ThreadCreated extends ThreadUpdate {
        //
    }

    @Entity
    @DiscriminatorValue(UpdateType.Resolved)
    public static class ThreadResolved extends ThreadUpdate {
        //
    }

    @Entity
    @DiscriminatorValue(UpdateType.AssignToTeam)
    public static class ThreadAssignToTeam extends ThreadUpdate {
        @ManyToOne
        @JoinColumn(name = "data.assigned_to_team_id")
        Team assignedToTeam;
    }

    @Entity
    @DiscriminatorValue(UpdateType.ClaimedByUser)
    public static class ThreadClaimedByUser extends ThreadUpdate {
        @ManyToOne
        @JoinColumn(name = "data.assigned_to_user_id")
        User assignedToUser;
    }
}
