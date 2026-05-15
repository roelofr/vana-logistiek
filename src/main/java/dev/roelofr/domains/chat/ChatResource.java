package dev.roelofr.domains.chat;

import dev.roelofr.domains.chat.dto.CreateChatRequest;
import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatRepository;
import dev.roelofr.domains.users.model.GroupRepository;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.users.model.UserRepository;
import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Map;

@Path("/chats")
@Authenticated
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class ChatResource {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Context
    SecurityContext securityContext;

    @Context
    JsonWebToken jwt;

    @GET
    @Path("/")
    public RestResponse<Map<String, Object>> index() {
        return RestResponse.ok(
            Map.ofEntries(
                Map.entry("auth-secure", securityContext.isSecure()),
                Map.entry("auth-scheme", securityContext.getAuthenticationScheme()),
                Map.entry("auth-principal", securityContext.getUserPrincipal()),
                Map.entry("claims", jwt.getClaimNames()),
                Map.entry("audiences", jwt.getAudience()),
                Map.entry("groups", jwt.getGroups())
            )
        );
    }

    @POST
    @Transactional
    public RestResponse<Chat> create(@Context User user, @Valid CreateChatRequest request) {
        var chatGroupIds = request.groups();
        var chatUserIds = request.users();

        var chatGroups = chatGroupIds.isEmpty() ? null : groupRepository.findByIds(chatGroupIds);
        var chatUsers = chatUserIds.isEmpty() ? null : userRepository.findByIds(chatUserIds);

        var chat = Chat.builder()
            .title(request.title())
            .build();

        if (chatGroups != null) chatGroups.forEach(chat::addGroup);
        if (chatUsers != null) chatUsers.forEach(chat::addUser);

        if (!chat.isVisibleForUser(user))
            chat.addUser(user);

        chatRepository.persist(chat);

        return RestResponse.ok(chat);
    }
}
