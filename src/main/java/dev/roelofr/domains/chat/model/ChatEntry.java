package dev.roelofr.domains.chat.model;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ChatEntry extends ChatModel {
    @ManyToOne
    @JoinColumn(name = "chat_id")
    Chat chat;
}

