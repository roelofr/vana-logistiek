package dev.roelofr.domains.chat.model;

import dev.roelofr.domains.users.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ChatReadEntryRepository implements PanacheRepository<ChatReadEntry> {
    public Optional<ChatReadEntry> findByChatAndUser(Chat chat, User user) {
        return find("user = ?1 AND chat = ?2", user, chat).singleResultOptional();
    }

    public ChatReadEntry findOrCreateByChatAndUser(Chat chat, User user) {
        return findByChatAndUser(chat, user)
            .orElseGet(() -> {
                var newEntry = ChatReadEntry.builder()
                    .user(user)
                    .chat(chat)
                    .build();

                persist(newEntry);

                return newEntry;
            });
    }

    public Map<Long, Long> listByUserAndChatIds(User user, List<Long> chatIds) {
        return find("#ChatReadEntry.findByUserForChats", Map.ofEntries(
            Map.entry("chats", chatIds),
            Map.entry("user", user)
        ))
            .stream()
            .collect(Collectors.toMap(ChatReadEntry::getChatId, ChatReadEntry::getEntryId));
    }
}
