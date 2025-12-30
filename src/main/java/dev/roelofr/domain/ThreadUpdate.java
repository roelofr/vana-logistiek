package dev.roelofr.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.roelofr.domain.enums.UpdateType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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
@DiscriminatorColumn(name = "type", length = 20)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class ThreadUpdate extends Model {
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "thread_id", nullable = false)
    Thread thread;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"team"})
    User user;

    @ManyToOne
    @JsonManagedReference
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
        @Lob
        @Column(name = "message")
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
        @JoinColumn(name = "entity_id")
        Team assignedToTeam;
    }

    @Entity
    @DiscriminatorValue(UpdateType.ClaimedByUser)
    public static class ThreadClaimedByUser extends ThreadUpdate {
        @ManyToOne
        @JoinColumn(name = "entity_id")
        User assignedToUser;
    }
}
