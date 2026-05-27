package dev.roelofr.domains.chat;

import dev.roelofr.domain.dto.Pagination;
import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatGroup;
import dev.roelofr.domains.chat.model.ChatRepository;
import dev.roelofr.domains.chat.model.ChatUser;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public Optional<Chat> findByKey(@NotNull @NotBlank String key) {
        return chatRepository.findByKey(key);
    }

    public boolean isVisibleForUser(@NotNull Chat chat, @NotNull User user) {
        return chat.getUsers().stream().anyMatch(cu -> cu.getUser().is(user))
            || chat.getGroups().stream().anyMatch(cg -> cg.getGroup().hasUser(user));
    }

    @Transactional
    public void addChatParticipant(@NotNull Chat chat, @NotNull User user) {
        var chatUsers = chat.getUsers();
        if (chatUsers.stream().anyMatch(chatUser -> chatUser.getUser().is(user)))
            return;

        chatUsers.add(ChatUser.create(chat, user));
    }

    @Transactional
    public void addChatParticipant(@NotNull Chat chat, @NotNull Group group) {
        var chatGroups = chat.getGroups();
        if (chatGroups.stream().anyMatch(chatGroup -> group.is(chatGroup.getGroup())))
            return;

        chatGroups.add(ChatGroup.create(chat, group));
    }

    public void addChatParticipantUnlessAccess(@NotNull Chat chat, @NotNull User user) {
        if (!isVisibleForUser(chat, user))
            addChatParticipant(chat, user);
    }

    @Transactional
    public Chat createChat(@NotNull @Length(min = 2, max = 200) String title, List<User> chatUsers, List<Group> chatGroups) {
        var chat = Chat.builder()
            .title(title)
            .build();

        if (chatGroups != null) chatGroups.forEach(chatGroup -> addChatParticipant(chat, chatGroup));
        if (chatUsers != null) chatUsers.forEach(chatUser -> addChatParticipant(chat, chatUser));

        chatRepository.persist(chat);

        return chat;
    }

    public Optional<Chat> findById(long id) {
        return chatRepository.findByIdOptional(id);
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
        return chatRepository.find("#Chat.findWithoutKeyByUserSorted", Map.of("user", user))
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
            .createNamedQuery("Chat.countWithoutKeyByUser", Long.class)
            .setParameter("user", user)
            .getResultList()
            .size();

        var totalPages = (int) Math.ceil((double) totalChats / pageSize);

        return new Pagination(totalChats, totalPages, pageNumber);
    }
}
