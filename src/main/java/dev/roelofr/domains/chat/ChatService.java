package dev.roelofr.domains.chat;

import dev.roelofr.domain.dto.Pagination;
import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatRepository;
import dev.roelofr.domains.chat.model.ChatType;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public Chat findByKey(@NotNull @NotBlank String key) {
        return chatRepository.findByKey(key).orElse(null);
    }

    public boolean isVisibleForUser(@NotNull Chat chat, @NotNull User user) {
        return chat.getUsers().stream().anyMatch(cu -> cu.is(user))
            || chat.getGroups().stream().anyMatch(cg -> cg.hasUser(user));
    }

    @Transactional
    public void addChatParticipant(@NotNull Chat chat, @NotNull User user) {
        var chatUsers = chat.getUsers();
        if (chatUsers.stream().anyMatch(chatUser -> chatUser.is(user)))
            return;

        chatUsers.add(user);
    }

    @Transactional
    public void addChatParticipant(@NotNull Chat chat, @NotNull Group group) {
        var chatGroups = chat.getGroups();
        if (chatGroups.stream().anyMatch(group::is))
            return;

        chatGroups.add(group);
    }

    public void addChatParticipantUnlessAccess(@NotNull Chat chat, @NotNull User user) {
        if (!isVisibleForUser(chat, user))
            addChatParticipant(chat, user);
    }

    @Transactional
    public Chat createChat(@NotNull ChatType type, @Nullable String key, @NotNull @Length(min = 2, max = 200) String title, Collection<User> chatUsers, Collection<Group> chatGroups) {
        var chat = Chat.builder()
            .title(title)
            .key(key)
            .type(type)
            .build();

        if (chatGroups != null) chatGroups.forEach(chatGroup -> addChatParticipant(chat, chatGroup));
        if (chatUsers != null) chatUsers.forEach(chatUser -> addChatParticipant(chat, chatUser));

        chatRepository.persist(chat);

        return chat;
    }

    @Transactional
    public Chat createChat(@NotNull @Length(min = 2, max = 200) String title, Collection<User> chatUsers, Collection<Group> chatGroups) {
        return createChat(ChatType.Regular, null, title, chatUsers, chatGroups);
    }

    @Transactional
    public Chat createChat(@NotNull ChatType type, @NotNull @Length(min = 2, max = 200) String title, Collection<User> chatUsers, Collection<Group> chatGroups) {
        return createChat(type, null, title, chatUsers, chatGroups);
    }

    public Chat findById(long id) {
        return chatRepository.findByIdOptional(id).orElse(null);
    }

    public List<Chat> findAllSorted() {
        return chatRepository.list("#Chat.findAllSorted");
    }

    /**
     * Returns a list of all IDs the user is able to open.
     */
    public List<Long> findIdsByUser(User user) {
        return chatRepository.find("#Chat.findIdsByUser", Map.of("user", user))
            .project(Long.class)
            .list();
    }

    /**
     * Find all chats this user is involved in, without those with a key (they are managed differently), and ordered
     * by their last update date.
     *
     * @param user       User to look for
     * @param pageNumber The 1-based page number to retrieve
     * @param pageSize   The number of items per page. This should be between 1 and 100
     * @return Chats that the user is involved in, without those with a key, ordered by the last update date and constrained to the page size given.
     */
    @Transactional
    public List<Chat> findWithoutKeyByUser(User user, int pageNumber, int pageSize) {
        return chatRepository.find("#Chat.findByUserSorted", Map.of("user", user))
            .page(pageNumber - 1, pageSize)
            .list();
    }

    /**
     * Returns the total number of chats this user is involved in, without those with a key. This method is slightly
     * expensive as we need to fetch all IDs.
     */
    @Transactional
    public Pagination paginateWithoutKeyByUser(User user, int pageNumber, int pageSize) {
        var totalChats = chatRepository.getEntityManager()
            .createNamedQuery("Chat.countByUser", Long.class)
            .setParameter("user", user)
            .getResultList()
            .size();

        var totalPages = (int) Math.ceil((double) totalChats / pageSize);

        return new Pagination(totalChats, totalPages, pageNumber);
    }

    public Group findRelevantGroup(Chat chat, User user) {
        var fromChat = chat.getGroups().stream().filter(cg -> cg.hasUser(user)).findFirst();

        if (fromChat.isPresent())
            return fromChat.get();

        if (user.getGroups().isEmpty())
            return null;

        return user.getGroups().stream().findFirst().orElse(null);
    }

    /**
     * Instruct the given chats to eagerly load their participants.
     */
    public long fetchParticipantsForChats(List<Chat> chats) {
        return fetchParticipantsForChatIds(chats.stream().map(Chat::getId).toList());
    }

    /**
     * Instruct the given chat IDs to eagerly load their participants.
     */
    public long fetchParticipantsForChatIds(List<Long> chatIds) {

        var userChats = chatRepository.list("#Chat.getChatsWithUsersById", Map.of("ids", chatIds));
        var groupChats = chatRepository.list("#Chat.getChatsWithGroupsById", Map.of("ids", chatIds));

        return userChats.size() + groupChats.size();
    }
}
