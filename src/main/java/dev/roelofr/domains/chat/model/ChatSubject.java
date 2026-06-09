package dev.roelofr.domains.chat.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import dev.roelofr.domain.Model;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@Table(name = "chat_subjects")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ChatSubject extends Model {
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id", nullable = false, updatable = false)
    @JsonIncludeProperties({"id", "name"})
    Chat chat;
}
