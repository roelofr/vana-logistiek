package dev.roelofr.domains.chat;

import dev.roelofr.domains.chat.dto.ChatDto;
import dev.roelofr.domains.chat.dto.ChatList;
import dev.roelofr.domains.chat.dto.ChatMemberDto;
import dev.roelofr.domains.chat.dto.ChatUserDto;
import dev.roelofr.domains.chat.dto.CreateChatRequest;
import dev.roelofr.domains.chat.dto.CreateEntryRequest;
import dev.roelofr.domains.chat.dto.MarkReadRequest;
import dev.roelofr.domains.chat.model.ChatEntry;
import dev.roelofr.domains.chat.model.ChatState;
import dev.roelofr.domains.chat.model.SystemMessageType;
import dev.roelofr.domains.issue.Issue;
import dev.roelofr.domains.users.model.GroupRepository;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.users.model.UserRepository;
import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.reactive.NoCache;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Path("/chats")
@Authenticated
@RequiredArgsConstructor
@Tags({@Tag(name = "Chat")})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChatResource {
    private static final int PAGE_DEFAULT = 1;
    private static final int PAGE_SIZE = 5_000;

    private final ChatResourceService resourceService;
    private final ChatEntryService chatEntryService;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final ChatChannelService chatChannelService;
    private final ChatReadService chatReadService;

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
    @Transactional
    public RestResponse<ChatList> list(@Context User user) {
        if (user == null)
            log.warn("The user is null!");

        var totalStatistics = chatService.paginateWithoutKeyByUser(user, PAGE_DEFAULT, PAGE_SIZE);

        if (totalStatistics.currentPage() > 1 && totalStatistics.currentPage() > totalStatistics.totalPages())
            return RestResponse.notFound();

        var chats = chatService.findWithoutKeyByUser(user, PAGE_DEFAULT, PAGE_SIZE);

        var lastEntries = chatEntryService.findLastEntryByChats(chats);
        var lastReadEntries = chatReadService.getMultipleByModel(user, chats);

        var unreadByChat = lastEntries.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                var chatId = entry.getKey(); // Long
                var lastEntry = entry.getValue(); // Long
                var lastReadEntry = lastReadEntries.get(chatId); // Long | null

                return lastEntry != null && !Objects.equals(lastEntry, lastReadEntry);
            }));

        return RestResponse.ok(
            new ChatList(
                chats.stream().map(chat -> new ChatDto(chat, unreadByChat.getOrDefault(chat.getId(), false))).toList(),
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

        chatService.fetchParticipantsForChatIds(List.of(chat.getId()));

        if (chat.getEntries() != null)
            chatReadService.set(user, chat, chat.getEntries().getLast());

        return RestResponse.ok(
            new ChatDto(chat)
        );
    }

    @GET
    @Transactional
    @Path("/by-id/{id}")
    @Operation(
        operationId = "chatFindById",
        description = "Finds a single chat by it's ID (numeric)"
    )
    public RestResponse<ChatDto> findById(@PathParam("id") @Positive long id, @Context User user) {
        return resourceService.chatToResponse(chatService.findById(id), user);
    }

    @GET
    @Transactional
    @Path("/by-id/{id}/users")
    @Operation(
        operationId = "chatFindUsersById",
        description = "Finds all users that take part in this chat, either directly or via a group"
    )
    public RestResponse<List<ChatUserDto>> findUsersInChatById(@PathParam("id") @Positive long id, @Context User user) {
        var chat = chatService.findById(id);
        if (chat == null)
            return RestResponse.notFound();

        if (!chatService.isVisibleForUser(chat, user))
            return RestResponse.status(Response.Status.FORBIDDEN);

        var chatUsers = new ArrayList<ChatUserDto>();

        chat.getUsers().forEach(chatUser -> chatUsers.add(new ChatUserDto(chatUser)));

        for (var group : chat.getGroups()) {
            group.getUsers().forEach(chatUser -> chatUsers.add(new ChatUserDto(chatUser, group)));
        }

        return RestResponse.ok(chatUsers);
    }

    @POST
    @Transactional
    @Path("/by-id/{id}/add-participants")
    @Operation(
        operationId = "chatAddParticipant",
        description = "Add one or more participants to the chat"
    )
    public RestResponse<List<ChatEntry>> addParticipants(@PathParam("id") @Positive long id, @Context User user, @NotNull @NotEmpty List<@Valid ChatMemberDto> members) {
        var chat = chatService.findById(id);
        if (chat == null)
            return RestResponse.notFound();

        if (!chatService.isVisibleForUser(chat, user))
            return RestResponse.status(Response.Status.FORBIDDEN);

        var chatGroupIds = members.stream().filter(ChatMemberDto::isGroup).map(ChatMemberDto::id).toList();
        var chatUserIds = members.stream().filter(ChatMemberDto::isUser).map(ChatMemberDto::id).toList();

        var chatGroups = chatGroupIds.isEmpty() ? null : groupRepository.mustFindByIds(chatGroupIds);
        var chatUsers = chatUserIds.isEmpty() ? null : userRepository.mustFindByIds(chatUserIds);

        var entries = new ArrayList<ChatEntry>(members.size());

        if (chatGroups != null) {
            var addGroupMessage = String.format("Groep %%s toegevoegd door %s", user.getName());
            for (var chatGroup : chatGroups) {
                chatService.addChatParticipant(chat, chatGroup);
                entries.add(chatEntryService.createSystemMessage(chat, SystemMessageType.GroupAdded, String.format(addGroupMessage, chatGroup.getName()), chatGroup));
            }
        }

        if (chatUsers != null) {
            var addUserMessage = String.format("Gebruiker %%s toegevoegd door %s", user.getName());
            for (var chatUser : chatUsers) {
                chatService.addChatParticipant(chat, chatUser);
                entries.add(chatEntryService.createSystemMessage(chat, SystemMessageType.UserAdded, String.format(addUserMessage, chatUser.getName()), chatUser));
            }
        }

        return RestResponse.ok(entries);
    }

    @POST
    @Transactional
    @Path("/by-id/{id}/close")
    @Operation(
        operationId = "chatClose",
        description = "Closes a chat, blocking any new messages from being added"
    )
    public RestResponse<Void> closeChatById(@PathParam("id") @Positive long id, @Context User user) {
        var chat = chatService.findById(id);
        if (chat == null)
            return RestResponse.notFound();

        if (!chatService.isVisibleForUser(chat, user))
            return RestResponse.status(Response.Status.FORBIDDEN);

        if (chat.getState().equals(ChatState.Closed))
            return RestResponse.status(Response.Status.RESET_CONTENT);

        if (chat.getState().equals(ChatState.Permanent))
            return RestResponse.status(Response.Status.BAD_REQUEST);

        if (chat.getSubject() instanceof Issue chatIssue && chatIssue.getResolvedAt() == null)
            return RestResponse.status(Response.Status.BAD_REQUEST);

        chat.setState(ChatState.Closed);
        chatEntryService.createSystemMessage(chat, SystemMessageType.Closed, "Chat afgesloten", user);

        return RestResponse.ok();
    }

    @GET
    @Transactional
    @Path("/by-key/{key}")
    @Operation(
        operationId = "chatFindByKey",
        description = "Finds a single chat by it's key (string)"
    )
    public RestResponse<ChatDto> findByKey(@PathParam("key") @NotBlank String key, @Context User user) {
        return resourceService.chatToResponse(chatService.findByKey(key), user);
    }

    @GET
    @Transactional
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
            chatEntryService.listByChat(chat)
        );
    }

    @POST
    @Transactional
    @Path("/by-id/{id}/entries")
    @Operation(
        operationId = "chatPostEntry",
        description = "Adds an entry to the chat, may be a combination of files, locations and a message."
    )
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public RestResponse<List<ChatEntry>> postEntry(@PathParam("id") @Positive long id, @Context User user, @Valid @NotNull CreateEntryRequest request) {
        log.info("Adding entry to chat {}", id);

        var chat = chatService.findById(id);
        if (chat == null) {
            log.info("Chat {} not found", id);
            return RestResponse.status(RestResponse.Status.NOT_FOUND);
        }

        if (!chatService.isVisibleForUser(chat, user)) {
            log.info("Chat {} not accessible to {}", id, user);
            return RestResponse.status(RestResponse.Status.FORBIDDEN);
        }

        var group = chatService.findRelevantGroup(chat, user);
        var groupingKey = UUID.randomUUID();

        var createdEntries = new ArrayList<ChatEntry>();

        if (request.hasFiles()) {
            for (var upload : request.files()) {
                createdEntries.add(chatEntryService.createChatFile(chat, groupingKey, user, group, upload));
            }
        }

        if (request.hasLocation())
            createdEntries.add(chatEntryService.createChatLocation(chat, groupingKey, user, group, request.location()));

        if (request.hasMessage())
            createdEntries.add(chatEntryService.createChatMessage(chat, groupingKey, user, group, request.message()));

        // Mark read until here
        if (!createdEntries.isEmpty())
            chatReadService.set(user, chat, createdEntries.getLast());

        return RestResponse.ok(createdEntries);
    }

    @POST
    @Transactional
    @Path("/by-id/{id}/read")
    @Operation(
        operationId = "chatPostRead",
        description = "Tells the system upto which item was read by the user."
    )
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Void> postRead(@PathParam("id") @Positive long id, @Context User user, @Valid @NotNull MarkReadRequest request) {
        var chat = chatService.findById(id);
        if (chat == null) {
            log.info("Chat {} not found", id);
            return RestResponse.status(RestResponse.Status.NOT_FOUND);
        }

        var entryId = request.entryId();
        var entry = chatEntryService.findByIdInChat(entryId, chat);
        if (entry == null) {
            log.info("Entry {} in chat {} was not found", entryId, id);
            return RestResponse.status(RestResponse.Status.NOT_FOUND);
        }

        log.info("User {} has read {} in chat {}", user.getName(), entry.getId(), chat.getId());
        chatReadService.set(user, chat, entry);

        return RestResponse.ok();
    }

    @GET
    @NoCache
    @Blocking
    @Path("/stream/{id}")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "chatStreamEntries",
        description = "Streams chat entries when they are created or updated."
    )
    public Multi<ChatEntry> streamById(@PathParam("id") long id, @Context User user) {
        var chat = chatService.findById(id);
        if (chat == null)
            throw new NotFoundException();

        if (!chatService.isVisibleForUser(chat, user))
            throw new ForbiddenException();

        return chatChannelService.getChatEntries()
            .filter(entry -> entry.getChat().getId() == id);
    }
}
