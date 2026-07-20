package dev.roelofr.domains.chat.dto;

import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.users.model.UserFlags;

public record ChatUserDto(
    long id,
    String name,
    String avatar,
    boolean atWork,
    ChatParticipantDto viaGroup
) {
    private ChatUserDto(User user, ChatParticipantDto via) {
        this(user.getId(), user.getName(), user.getAvatar(), user.hasFlag(UserFlags.Active), via);

    }

    public ChatUserDto(User user, Group viaGroup) {
        this(user, new ChatParticipantDto(viaGroup));
    }

    public ChatUserDto(User user) {
        this(user, (ChatParticipantDto) null);
    }
}
