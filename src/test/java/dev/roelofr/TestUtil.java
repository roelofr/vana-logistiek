package dev.roelofr;

import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatRepository;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.GroupRepository;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.users.model.UserRepository;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
@RequiredArgsConstructor
public class TestUtil {
    private static final AtomicInteger ChatIncrementer = new AtomicInteger(1);
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ChatRepository chatRepository;

    protected User createOrFindUser(String providerId) {
        return userRepository.findByProviderId(providerId)
            .orElseGet(() -> {
                var newUser = User.builder()
                    .name(providerId)
                    .email("%s@example.com".formatted(providerId))
                    .providerId(providerId)
                    .build();

                QuarkusTransaction.requiringNew().run(() -> userRepository.persist(newUser));

                return newUser;
            });
    }

    protected Group createOrFindGroup(String name) {
        return groupRepository.findByName(name)
            .orElseGet(() -> {
                var newGroup = Group.builder()
                    .name(name)
                    .build();


                QuarkusTransaction.requiringNew().run(() -> groupRepository.persist(newGroup));

                return newGroup;
            });
    }

    public User createUser(String providerId, List<String> groups) {
        var user = createOrFindUser(providerId);

        if (groups != null)
            user.setGroups(
                groups.stream().map(this::createOrFindGroup).toList()
            );

        return user;
    }

    public Group createGroup(String name, List<String> users) {
        var group = createOrFindGroup(name);

        if (users != null)
            group.setUsers(
                users.stream().map(this::createOrFindUser).toList()
            );

        return group;
    }

    public Chat createChat(List<String> users, List<String> groups) {
        return createChat(String.format("Test Chat %06d", ChatIncrementer.getAndIncrement()), users, groups);
    }

    public Chat createChat(String name, List<String> users, List<String> groups) {
        var newChat = Chat.builder()
            .title(name)
            .build();

        if (users != null)
            users.stream().map(this::createOrFindUser).forEach(newChat::addUser);
        if (groups != null)
            groups.stream().map(this::createOrFindGroup).forEach(newChat::addGroup);

        QuarkusTransaction.requiringNew().run(() -> chatRepository.persist(newChat));

        return newChat;
    }
}
