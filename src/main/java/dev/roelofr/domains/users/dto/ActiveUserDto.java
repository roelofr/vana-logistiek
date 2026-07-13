package dev.roelofr.domains.users.dto;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;

import java.util.Set;

public record ActiveUserDto(
    long id,
    String name,
    String email,
    String avatar,
    @JsonIncludeProperties({"id", "name", "colour", "icon"})
    Set<Group> groups
) {
    public ActiveUserDto(User user, Set<Group> userGroups) {
        this(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getAvatar(),
            userGroups
        );
    }
}
