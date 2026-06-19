package dev.roelofr.domains.chat;

import dev.roelofr.domains.chat.dto.ChatDto;
import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.users.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
class ChatResourceService {
    private final ChatService chatService;

    public RestResponse<ChatDto> chatToResponse(Chat chat, User user) {
        if (chat == null) {
            log.info("Chat not found");
            return RestResponse.notFound();
        }

        if (!chatService.isVisibleForUser(chat, user)) {
            log.info("Chat #{} not visible to user {}", chat.getId(), user.getName());
            return RestResponse.status(Response.Status.FORBIDDEN);
        }

        log.info("Returning chat #{} for user {}", chat.getId(), user.getName());

        return RestResponse.ok(new ChatDto(chat));
    }
}
