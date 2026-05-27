package dev.roelofr.domains.chat;

import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.users.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Optional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
class ChatResourceService {
    private final ChatService chatService;

    public RestResponse<Chat> chatToResponse(Optional<Chat> chat, User user) {
        if (chat.isEmpty())
            return RestResponse.notFound();

        var chatObj = chat.get();
        if (!chatService.isVisibleForUser(chatObj, user))
            return RestResponse.status(Response.Status.FORBIDDEN);

        return RestResponse.ok(chatObj);
    }
}
