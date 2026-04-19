package dev.roelofr.domains.chat.model;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@DiscriminatorColumn(name = "type", length = 16)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class ChatEntry extends ChatModel {
    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false, updatable = false)
    Chat chat;
}
