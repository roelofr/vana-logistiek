package dev.roelofr.domains.chat.model;

import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@DiscriminatorValue(value = ChatMessage.TYPE)
public class ChatMessage extends ChatEntry {
    public static final String TYPE = "message";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", updatable = false)
    public User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", updatable = false)
    public Group group;

    public String message;
}
