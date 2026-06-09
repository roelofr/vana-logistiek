package dev.roelofr.domains.chat;

import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatRepository;
import dev.roelofr.domains.chat.model.ChatState;
import dev.roelofr.domains.chat.model.ChatType;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {
    @Mock
    ChatRepository chatRepository;

    @InjectMocks
    ChatService chatService;

    @Test
    void isVisibleForUserDirectly() {
        var chat = Chat.builder().build();
        var user = User.builder().id(1L).build();

        chat.addUser(user);

        assertTrue(chatService.isVisibleForUser(chat, user));
    }

    @Test
    void isVisibleForUserViaGroup() {
        var chat = Chat.builder().build();
        var user = User.builder().id(1L).build();
        var group = Group.builder()
            .users(List.of(user))
            .build();

        chat.addGroup(group);

        assertTrue(chatService.isVisibleForUser(chat, user));
    }

    @Test
    void isNotVisibleForUserOutOfLeague() {
        var chat = Chat.builder().build();
        var user1 = User.builder().id(1L).build();
        var user2 = User.builder().id(2L).build();
        var user3 = User.builder().id(3L).build();

        var group = Group.builder()
            .users(List.of(user3))
            .build();

        chat.addUser(user2);
        chat.addGroup(group);

        assertFalse(chatService.isVisibleForUser(chat, user1));
    }

    @Test
    void addChatParticipantUser() {
        var user1 = User.builder().id(1L).build();
        var user2 = User.builder().id(2L).build();

        var chat = Chat.builder().build();

        assertTrue(chat.getUsers().isEmpty());
        assertTrue(chat.getGroups().isEmpty());

        chatService.addChatParticipant(chat, user1);

        assertEquals(1, chat.getUsers().size());
        assertTrue(chat.getGroups().isEmpty());

        chatService.addChatParticipant(chat, user1);

        assertEquals(1, chat.getUsers().size());
        assertTrue(chat.getGroups().isEmpty());

        chatService.addChatParticipant(chat, user2);

        assertEquals(2, chat.getUsers().size());
        assertTrue(chat.getGroups().isEmpty());
    }

    @Test
    void addChatParticipantGroup() {
        var group1 = Group.builder().id(1L).build();
        var group2 = Group.builder().id(2L).build();

        var chat = Chat.builder().build();

        assertTrue(chat.getUsers().isEmpty());
        assertTrue(chat.getGroups().isEmpty());

        chatService.addChatParticipant(chat, group1);

        assertTrue(chat.getUsers().isEmpty());
        assertEquals(1, chat.getGroups().size());

        chatService.addChatParticipant(chat, group1);

        assertTrue(chat.getUsers().isEmpty());
        assertEquals(1, chat.getGroups().size());

        chatService.addChatParticipant(chat, group2);

        assertTrue(chat.getUsers().isEmpty());
        assertEquals(2, chat.getGroups().size());
    }

    @Test
    void addChatParticipantUnlessAccess() {
        var user1 = User.builder().id(1L).build();
        var user2 = User.builder().id(2L).build();

        var group1 = Group.builder().users(List.of(user1)).build();

        var chat = Chat.builder().build();

        chat.addGroup(group1);

        assertEquals(0, chat.getUsers().size());
        assertEquals(1, chat.getGroups().size());

        chatService.addChatParticipantUnlessAccess(chat, user1);

        assertEquals(0, chat.getUsers().size());
        assertEquals(1, chat.getGroups().size());

        chatService.addChatParticipantUnlessAccess(chat, user2);

        assertEquals(1, chat.getUsers().size());
        assertEquals(1, chat.getGroups().size());
    }

    @Test
    void createChatWithTypeKeyUsersAndGroups() {
        var user1 = User.builder().id(1L).build();
        var user2 = User.builder().id(2L).build();

        var group1 = Group.builder().users(List.of(user1)).build();

        var chat = chatService.createChat(ChatType.Group, "test", "Hello World", List.of(user1, user2), List.of(group1));

        assertNotNull(chat);
        assertEquals(ChatType.Group, chat.getType());
        assertEquals(ChatState.Active, chat.getState());
        assertEquals("test", chat.getKey());
        assertEquals("Hello World", chat.getTitle());

        assertEquals(2, chat.getUsers().size());
        assertEquals(1, chat.getGroups().size());

        BDDMockito.then(chatRepository).should().persist(chat);
    }

    @Test
    void createChatWithMinimum() {
        var chat = chatService.createChat("Hello World", null, null);

        assertNotNull(chat);
        assertEquals(ChatType.Regular, chat.getType());
        assertEquals(ChatState.Active, chat.getState());
        assertNull(chat.getKey());
        assertEquals("Hello World", chat.getTitle());

        assertTrue(chat.getUsers().isEmpty());
        assertTrue(chat.getGroups().isEmpty());

        BDDMockito.then(chatRepository).should().persist(chat);
    }
}
