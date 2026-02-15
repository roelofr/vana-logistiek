package dev.roelofr.rest.mappers;

import dev.roelofr.domain.ThreadUpdate;
import dev.roelofr.domain.User;
import dev.roelofr.rest.dtos.ThreadMessage;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ThreadMessageMapper {
    public List<ThreadMessage> mapUpdatesToMessages(List<ThreadUpdate> updates, User actingAs) {
        return updates.stream()
            .map(update -> {
                var message = mapUpdateToMessage(update);
                if (update.getUser().is(actingAs))
                    message.setMe(true);
                return message;
            })
            .toList();
    }

    private ThreadMessage mapUpdateToMessage(ThreadUpdate update) {
        var builder = ThreadMessage.builder()
            .user(update.getUser())
            .team(update.getTeam())
            .date(update.getCreatedAt())
            .updateType(update.getType())
            .update(update);

        if (update instanceof ThreadUpdate.ThreadMessage updateMessage)
            return builder.type(ThreadMessage.MessageType.Chat)
                .message(updateMessage.getMessage())
                .build();

        if (update instanceof ThreadUpdate.ThreadAttachment attachment)
            return builder
                .type(ThreadMessage.MessageType.Image)
                .message(attachment.getFilename())
                .build();

        if (update instanceof ThreadUpdate.ThreadResolved)
            return builder
                .type(ThreadMessage.MessageType.Resolved)
                .message(String.format("Melding gemarkeerd als opgeslost door %s", update.getUser().getName()))
                .build();


        builder.type(ThreadMessage.MessageType.System);

        if (update instanceof ThreadUpdate.ThreadCreated)
            return builder
                .message(String.format("Melding aangemaakt door %s", update.getUser().getName()))
                .build();

        if (update instanceof ThreadUpdate.ThreadClaimedByUser claimed)
            return builder
                .message(String.format("Melding opgepakt door %s", claimed.getAssignedToUser().getName()))
                .build();

        if (update instanceof ThreadUpdate.ThreadAssignToTeam assigned)
            return builder
                .message(String.format("Melding toegewezen aan team %s door %s", assigned.getAssignedToTeam().getName(), update.getUser().getName()))
                .build();

        return builder.message("Onbekende mutatie").build();
    }
}
