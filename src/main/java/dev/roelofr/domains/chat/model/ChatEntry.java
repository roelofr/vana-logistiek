package dev.roelofr.domains.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entries to a chat, like messages, files and system notices.
 * The groupId is used to group messages sent at the same time together.
 * Grouping should only happen if the IDs are identical between the current and the previous message, and where
 * an actor is known.
 */
@Data
@Entity
@Table(name = "chat_entries")
@SuperBuilder
@NoArgsConstructor
@Schema(
    description = "A message posted in a chat",
    oneOf = {ChatMessage.class, ChatFile.class}
)
@EqualsAndHashCode(callSuper = true)
@DiscriminatorColumn(name = "entry_type", length = 16, columnDefinition = "varchar(16)")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class ChatEntry extends ChatModel {
    @ManyToOne
    @OrderBy("createdAt")
    @JoinColumn(name = "chat_id", nullable = false, updatable = false)
    @JsonIncludeProperties({"id", "name"})
    Chat chat;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "grouping_key")
    UUID groupingKey = null;

    @PrePersist
    void setGroupingKeyIfUnset() {
        if (groupingKey == null)
            groupingKey = UUID.randomUUID();
    }

    @JsonInclude
    abstract String getType();
}
