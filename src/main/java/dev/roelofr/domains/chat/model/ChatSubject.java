package dev.roelofr.domains.chat.model;

import dev.roelofr.domain.Model;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChatSubject extends Model {
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id", nullable = false, updatable = false)
    protected Chat chat;
}
