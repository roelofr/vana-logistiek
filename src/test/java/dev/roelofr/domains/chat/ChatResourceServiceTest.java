package dev.roelofr.domains.chat;

import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatRepository;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ChatResourceServiceTest {
    ChatResourceService chatResourceService;
    User user;
    Chat chat;

    @BeforeEach
    void makeUserAndChat() {
        user = User.builder()
            .id(42L)
            .name("Test")
            .build();

        chat = Chat.builder()
            .id(6L)
            .title("Hello World")
            .build();

        var chatService = new ChatService(mock(ChatRepository.class));
        chatResourceService = new ChatResourceService(chatService);
    }

    @Test
    void chatToResponseOk() {
        // Grant access directly
        chat.getUsers().add(user);

        var response = assertDoesNotThrow(() -> chatResourceService.chatToResponse(chat, user));
        assertEquals(200, response.getStatus());
    }

    @Test
    void chatToResponseOkViaGroup() {
        var group = Group.builder().name("Test Group").users(List.of(user)).build();
        chat.getGroups().add(group);

        var response = assertDoesNotThrow(() -> chatResourceService.chatToResponse(chat, user));
        assertEquals(200, response.getStatus());
    }

    @Test
    void chatToResponseNotAuthorized() {
        var group = Group.builder().name("Test Group").build();
        chat.getGroups().add(group);

        var response = assertDoesNotThrow(() -> chatResourceService.chatToResponse(chat, user));
        assertEquals(403, response.getStatus());
    }

    @Test
    void chatToResponseNotFound() {
        var response = assertDoesNotThrow(() -> chatResourceService.chatToResponse(null, null));
        assertEquals(404, response.getStatus());
    }
}
