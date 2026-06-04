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
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Slf4j
@Path("/chats")
@Authenticated
@RequiredArgsConstructor
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
    public RestResponse<ChatList> index(@Context User user) {
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
                totalStatistics,
                chats.stream().map(ChatList.ChatListChat::new).toList()
            )
        );
    }

    @POST
    @Transactional
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
    public RestResponse<ChatDto> getById(@PathParam("id") @Positive long id, @Context User user) {
        return resourceService.chatToResponse(chatService.findById(id), user);
    }

    @GET
    @Path("/by-key/{key}")
    public RestResponse<ChatDto> getById(@PathParam("key") @NotBlank String key, @Context User user) {
        return resourceService.chatToResponse(chatService.findByKey(key), user);
    }

    @GET
    @Path("/by-id/{id}/entries")
    public RestResponse<List<ChatEntry>> getEntries(@PathParam("id") @Positive long id, @Context User user) {
        return RestResponse.status(RestResponse.Status.NOT_IMPLEMENTED);
    }

}
