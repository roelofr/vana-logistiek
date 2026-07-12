package dev.roelofr.domains.chat.dto;

import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import jakarta.annotation.Nonnull;

public record ChatParticipantDto(
    @Nonnull Long id,
    @Nonnull String name,
    String avatar,
    String icon,
    String colour
) {
    public ChatParticipantDto(Group group) {
        this(
            group.getId(),
            group.getName(),
            null,
            group.getIcon(),
            group.getColour()
        );
    }

    public ChatParticipantDto(User user) {
        this(
            user.getId(),
            user.getName(),
            user.getAvatar(),
            null,
            null
        );
    }
}
