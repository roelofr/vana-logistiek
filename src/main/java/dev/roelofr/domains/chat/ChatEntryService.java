package dev.roelofr.domains.chat;

import dev.roelofr.domains.chat.dto.ChatLocationDto;
import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatEntry;
import dev.roelofr.domains.chat.model.ChatEntryRepository;
import dev.roelofr.domains.chat.model.ChatFile;
import dev.roelofr.domains.chat.model.ChatLocation;
import dev.roelofr.domains.chat.model.ChatMessage;
import dev.roelofr.domains.chat.model.ChatSystemMessage;
import dev.roelofr.domains.chat.model.SystemMessageType;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.events.ChatFileUploaded;
import dev.roelofr.service.FileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
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
    private final Event<ChatFileUploaded> chatFileUploadDispatcher;
    private final ChatChannelService chatChannelService;

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

        chatChannelService.sendChatEntry(entry);

        return entry;
    }

    @Transactional
    public ChatEntry createChatFile(Chat chat, UUID groupingKey, User user, Group group, FileUpload upload) {
        var file = fileService.persistWebFile(user, upload);

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

        chatChannelService.sendChatEntry(entry);

        chatFileUploadDispatcher.fire(new ChatFileUploaded(entry));

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

        chatChannelService.sendChatEntry(entry);

        return entry;
    }

    @Transactional
    protected ChatEntry createSystemMessage(Chat chat, SystemMessageType type, String message, User subjectUser, Group subjectGroup) {
        var entry = ChatSystemMessage.builder()
            .chat(chat)
            .messageType(type)
            .message(message)
            .subjectUser(subjectUser)
            .subjectGroup(subjectGroup)
            .build();

        chatEntryRepository.persist(entry);

        chatChannelService.sendChatEntry(entry);

        return entry;
    }

    @Transactional
    public ChatEntry createSystemMessage(Chat chat, SystemMessageType type, String message, User subject) {
        return createSystemMessage(chat, type, message, subject, null);
    }

    @Transactional
    public ChatEntry createSystemMessage(Chat chat, SystemMessageType type, String message, Group subject) {
        return createSystemMessage(chat, type, message, null, subject);
    }

    @Transactional
    public ChatEntry createSystemMessage(Chat chat, SystemMessageType type, String message) {
        return createSystemMessage(chat, type, message, null, null);
    }
}
