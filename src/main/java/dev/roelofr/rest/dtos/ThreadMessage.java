package dev.roelofr.rest.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import dev.roelofr.domain.Team;
import dev.roelofr.domain.ThreadUpdate;
import dev.roelofr.domain.User;
import dev.roelofr.domain.enums.UpdateType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ThreadMessage {
    private String icon;
    private String message;
    private MessageType type;
    private UpdateType updateType;
    private LocalDateTime date;

    @JsonIncludeProperties({"id", "name"})
    private User user;

    @JsonIncludeProperties({"id", "name", "icon", "colour"})
    private Team team;

    @Setter
    private boolean isMe;

    @JsonIgnoreProperties({"thread", "user", "team"})
    private ThreadUpdate update;

    public enum MessageType {
        Chat,
        Image,
        System,
        Resolved
    }
}
