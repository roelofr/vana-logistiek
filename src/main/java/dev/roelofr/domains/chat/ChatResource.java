package dev.roelofr.domains.chat;

import dev.roelofr.domains.chat.dto.ChatDto;
import dev.roelofr.domains.chat.dto.ChatList;
import dev.roelofr.domains.chat.dto.CreateChatRequest;
import dev.roelofr.domains.chat.model.ChatEntry;
import dev.roelofr.domains.users.UserService;
import dev.roelofr.domains.users.model.GroupRepository;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.users.model.UserRepository;
import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Slf4j
@Path("/chats")
@Authenticated
@RequiredArgsConstructor
@Tags({@Tag(name = "Chat")})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChatResource {
    private final ChatResourceService resourceService;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final UserService userService;

    @GET
    @Path("/")
    @Operation(operationId = "chatList",
        description = "Lists all chat the user has access to, in order of last updated"
    )
    @APIResponse(
        name = "OK",
        content = {@Content(schema = @Schema(implementation = ChatList.class))},
        description = "The list of chats, including pagination information"
    )
    public RestResponse<ChatList> list(@Context User user) {
        int pageNumber = 1;
        int pageSize = 100;

        if (user == null)
            log.warn("The user is null!");
        else
            log.info("Listing {} chats on page {} for {}", pageSize, pageNumber, user.getName());

        var totalStatistics = chatService.paginateWithoutKeyByUser(user, pageNumber, pageSize);

        if (totalStatistics.currentPage() > 1 && totalStatistics.currentPage() > totalStatistics.totalPages())
            return RestResponse.notFound();

        var chats = chatService.findWithoutKeyByUser(user, pageNumber, pageSize);

        return RestResponse.ok(
            new ChatList(
                chats.stream().map(ChatList.ChatListChat::new).toList(),
                totalStatistics
            )
        );
    }

    @POST
    @Transactional
    @Operation(
        operationId = "chatCreate",
        description = "Creates a new chat"
    )
    @APIResponse(
        name = "Chat Created",
        responseCode = "200",
        content = {@Content(schema = @Schema(implementation = ChatDto.class))},
        description = "A new chat was created successfully"
    )
    public RestResponse<ChatDto> create(@Context User user, @Valid CreateChatRequest request) {
        assert user != null;

        var chatGroupIds = request.groups();
        var chatUserIds = request.users();

        var chatGroups = chatGroupIds.isEmpty() ? null : groupRepository.mustFindByIds(chatGroupIds);
        var chatUsers = chatUserIds.isEmpty() ? null : userRepository.mustFindByIds(chatUserIds);

        var chat = chatService.createChat(
            request.title(),
            chatUsers,
            chatGroups
        );

        chatService.addChatParticipantUnlessAccess(chat, user);

        return RestResponse.ok(
            new ChatDto(chat)
        );
    }

    @GET
    @Path("/by-id/{id}")
    @Operation(
        operationId = "chatFindById",
        description = "Finds a single chat by it's ID (numeric)"
    )
    public RestResponse<ChatDto> findById(@PathParam("id") @Positive long id, @Context User user) {
        return resourceService.chatToResponse(chatService.findById(id), user);
    }

    @GET
    @Path("/by-key/{key}")
    @Operation(
        operationId = "chatFindByKey",
        description = "Finds a single chat by it's key (string)"
    )
    public RestResponse<ChatDto> findByKey(@PathParam("key") @NotBlank String key, @Context User user) {
        return resourceService.chatToResponse(chatService.findByKey(key), user);
    }

    @GET
    @Path("/by-id/{id}/entries")
    @Operation(
        operationId = "chatGetEntries",
        description = "Returns the entries for a chat (looked up by ID), in chronological order"
    )
    public RestResponse<List<ChatEntry>> getEntries(@PathParam("id") @Positive long id, @Context User user) {
        var chat = chatService.findById(id);
        if (chat == null)
            return RestResponse.status(RestResponse.Status.NOT_FOUND);

        if (!chatService.isVisibleForUser(chat, user))
            return RestResponse.status(RestResponse.Status.FORBIDDEN);

        return RestResponse.ok(
            chat.getEntries()
        );
    }

}
