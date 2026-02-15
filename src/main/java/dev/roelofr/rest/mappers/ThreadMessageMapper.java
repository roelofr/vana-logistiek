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
            .id(update.getId())
            .user(update.getUser())
            .team(update.getTeam())
            .thread(update.getThread())
            .date(update.getCreatedAt())
            .updateType(update.getType())
            .update(update);

        switch (update) {
            case ThreadUpdate.ThreadMessage updateMessage -> {
                return builder.type(ThreadMessage.MessageType.Chat)
                    .message(updateMessage.getMessage())
                    .build();
            }
            case ThreadUpdate.ThreadAttachment attachment -> {
                return builder
                    .type(ThreadMessage.MessageType.Image)
                    .message(attachment.getFilename())
                    .build();
            }
            case ThreadUpdate.ThreadResolved threadResolved -> {
                return builder
                    .type(ThreadMessage.MessageType.Resolved)
                    .message(String.format("Melding gemarkeerd als opgelost door %s", update.getUser().getName()))
                    .build();
            }
            default -> {
            }
        }


        builder.type(ThreadMessage.MessageType.System);

        switch (update) {
            case ThreadUpdate.ThreadCreated threadCreated -> {
                return builder
                    .message(String.format("Melding aangemaakt door %s", update.getUser().getName()))
                    .build();
            }
            case ThreadUpdate.ThreadClaimedByUser claimed -> {
                return builder
                    .message(String.format("Melding opgepakt door %s", claimed.getAssignedToUser().getName()))
                    .build();
            }
            case ThreadUpdate.ThreadAssignToTeam assigned -> {
                return builder
                    .message(String.format("Melding toegewezen aan team %s door %s", assigned.getAssignedToTeam().getName(), update.getUser().getName()))
                    .build();
            }
            default -> {
            }
        }

        log.info("Attachment with ID {} could not be matched to a type", update.getId());
        log.info("Attachment type = {}", update.getType());

        return builder.message("Onbekende mutatie").build();
    }
}
