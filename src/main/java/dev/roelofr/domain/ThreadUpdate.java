package dev.roelofr.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.roelofr.domain.enums.FileStatus;
import dev.roelofr.domain.enums.UpdateType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "thread_updates")
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorColumn(name = "type", length = 20)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NamedQueries({
    @NamedQuery(
        name = "ThreadUpdate.findByThreadSorted",
        query = """
                SELECT tu
                FROM ThreadUpdate tu
                LEFT JOIN FETCH tu.user
                LEFT JOIN FETCH tu.team
                WHERE tu.thread = :thread
                ORDER BY tu.createdAt ASC
            """
    )
})
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

    @Builder.Default
    @Column(name = "group_key", length = 40)
    UUID groupKey = UUID.randomUUID();

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @PrePersist
    void setCreationTimestamps() {
        createdAt = LocalDateTime.now();
    }

    @JsonInclude
    abstract public UpdateType getType();

    @Getter
    @Setter
    @Entity
    @SuperBuilder
    @NoArgsConstructor
    @DiscriminatorValue(UpdateType.Types.Message)
    public static class ThreadMessage extends ThreadUpdate {
        @Lob
        @Column(name = "message")
        String message;

        @Override
        public UpdateType getType() {
            return UpdateType.Message;
        }
    }

    @Getter
    @Setter
    @Entity
    @SuperBuilder
    @NoArgsConstructor
    @DiscriminatorValue(UpdateType.Types.Attachment)
    public static class ThreadAttachment extends ThreadUpdate {
        @JsonIgnore
        @Column(name = "file_path", length = 200)
        String path;

        @Column(name = "file_name", length = 200)
        String filename;

        @Builder.Default
        @Enumerated(EnumType.STRING)
        @Column(name = "file_status", length = 10)
        FileStatus fileStatus = FileStatus.New;

        @Override
        public UpdateType getType() {
            return UpdateType.Attachment;
        }

        @JsonIgnore
        public Path getFilePath() {
            return Path.of(path);
        }

        public void setFilePath(Path path) {
            this.path = path.toString();
        }

        public boolean isFileReady() {
            return fileStatus == FileStatus.Ready;
        }
    }

    @Entity
    @SuperBuilder
    @NoArgsConstructor
    @DiscriminatorValue(UpdateType.Types.Created)
    public static class ThreadCreated extends ThreadUpdate {
        @Override
        public UpdateType getType() {
            return UpdateType.Created;
        }
    }

    @Entity
    @SuperBuilder
    @NoArgsConstructor
    @DiscriminatorValue(UpdateType.Types.Resolved)
    public static class ThreadResolved extends ThreadUpdate {
        @Override
        public UpdateType getType() {
            return UpdateType.Resolved;
        }
    }

    @Getter
    @Setter
    @Entity
    @SuperBuilder
    @NoArgsConstructor
    @DiscriminatorValue(UpdateType.Types.AssignToTeam)
    public static class ThreadAssignToTeam extends ThreadUpdate {
        @ManyToOne
        @JoinColumn(name = "entity_id")
        Team assignedToTeam;

        @Override
        public UpdateType getType() {
            return UpdateType.AssignToTeam;
        }
    }

    @Getter
    @Setter
    @Entity
    @SuperBuilder
    @NoArgsConstructor
    @DiscriminatorValue(UpdateType.Types.ClaimedByUser)
    public static class ThreadClaimedByUser extends ThreadUpdate {
        @ManyToOne
        @JoinColumn(name = "entity_id")
        User assignedToUser;

        @Override
        public UpdateType getType() {
            return UpdateType.ClaimedByUser;
        }
    }
}
