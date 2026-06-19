package dev.roelofr.domains.chat;

import dev.roelofr.domains.chat.dto.ChatLocationDto;
import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatEntry;
import dev.roelofr.domains.chat.model.ChatEntryRepository;
import dev.roelofr.domains.chat.model.ChatFile;
import dev.roelofr.domains.chat.model.ChatLocation;
import dev.roelofr.domains.chat.model.ChatMessage;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.service.FileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ChatEntryService {
    private final ChatEntryRepository chatEntryRepository;
    private final FileService fileService;

    public List<ChatEntry> listByChat(Chat chat) {
        return chatEntryRepository.list("#ChatEntry.listEagerByChat", Map.of("chat", chat));
    }

    public ChatEntry findById(long id) {
        return chatEntryRepository.findById(id);
    }

    @Transactional
    public ChatEntry createChatMessage(Chat chat, UUID groupingKey, User user, Group group, String message) {
        var entry = ChatMessage.builder()
            .groupingKey(groupingKey)
            .chat(chat)
            .user(user)
            .group(group)
            .message(message)
            .build();

        chatEntryRepository.persist(entry);

        return entry;
    }

    @Transactional
    public ChatEntry createChatFile(Chat chat, UUID groupingKey, User user, Group group, FileUpload upload) {
        var file = fileService.persistUpload(upload);

        var entry = ChatFile.builder()
            .groupingKey(groupingKey)
            .chat(chat)
            .user(user)
            .group(group)
            .path(file.toString())
            .filename(upload.fileName())
            .mimetype(upload.contentType())
            .build();

        chatEntryRepository.persist(entry);

        return entry;
    }

    @Transactional
    public ChatEntry createChatLocation(Chat chat, UUID groupingKey, User user, Group group, ChatLocationDto location) {
        var entry = ChatLocation.builder()
            .groupingKey(groupingKey)
            .chat(chat)
            .user(user)
            .group(group)
            .latitude(location.latitude())
            .longitude(location.longitude())
            .build();

        chatEntryRepository.persist(entry);

        return entry;
    }
}
