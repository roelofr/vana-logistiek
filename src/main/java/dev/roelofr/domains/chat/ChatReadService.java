package dev.roelofr.domains.chat;

import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatEntry;
import dev.roelofr.domains.chat.model.ChatReadEntry;
import dev.roelofr.domains.chat.model.ChatReadEntryRepository;
import dev.roelofr.domains.users.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ChatReadService {
    private final ChatReadEntryRepository repository;

    public Long get(User user, Chat chat) {
        return repository.findByChatAndUser(chat, user)
            .map(ChatReadEntry::getEntryId)
            .orElse(null);
    }

    public Map<Long, Long> getMultipleByModel(User user, List<Chat> chats) {
        return repository.listByUserAndChatIds(user, chats.stream().map(Chat::getId).toList());
    }

    public Map<Long, Long> getMultipleById(User user, List<Long> chatIds) {
        return repository.listByUserAndChatIds(user, chatIds);
    }

    @Transactional
    public void set(User user, Chat chat, ChatEntry chatEntry) {
        var entry = repository.findOrCreateByChatAndUser(chat, user);

        entry.setEntry(chatEntry);
    }
}
