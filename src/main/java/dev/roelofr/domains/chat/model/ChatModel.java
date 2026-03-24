package dev.roelofr.domains.chat.model;

import dev.roelofr.domain.Model;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
public abstract class ChatModel extends Model {
    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id", updatable = false, nullable = false)
    Chat chat;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
