package dev.roelofr;

import dev.roelofr.domains.users.Group;
import dev.roelofr.domains.users.GroupRepository;
import dev.roelofr.domains.users.User;
import dev.roelofr.domains.users.UserRepository;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TestProvisioning {
    public final static Map<String, List<String>> usersWithRoles = Map.ofEntries(
        Map.entry("test", List.of()),
        Map.entry("cp", List.of(Roles.CentralePost)),
        Map.entry("wijk1a", List.of(Roles.Wijkhouder)),
        Map.entry("wijk1b", List.of(Roles.Wijkhouder)),
        Map.entry("wijk2a", List.of(Roles.Wijkhouder)),
        Map.entry("gator1", List.of(Roles.Gedelegeerd)),
        Map.entry("gator2", List.of(Roles.Gedelegeerd)),
        Map.entry("admin", List.of(Roles.Admin))
    );

    public final static Map<String, List<String>> groupsWithUsers = Map.ofEntries(
        Map.entry("role", List.of("test")),
        Map.entry("wijk1", List.of("wijk1a", "wijk1b")),
        Map.entry("wijk2", List.of("wijk2a", "wijk2b")),
        Map.entry("gator", List.of("gator1", "gator2"))
    );

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Startup
    void provisionUsersAndGroups() {
        Map<String, User> mappedUsers = createAndMapUsers(usersWithRoles);

        createAndMapGroups(groupsWithUsers, mappedUsers);
    }

    @Transactional
    Map<String, User> createAndMapUsers(Map<String, List<String>> users) {
        return users.entrySet().stream()
            .map(userRoleMap -> {
                var username = userRoleMap.getKey();
                var userRoles = userRoleMap.getValue();
                var email = String.format("%s@example.com", username);
                return userRepository.findByEmailOptional(email)
                    .orElseGet(() -> {
                        var user = User.builder()
                            .name(username)
                            .email(email)
                            .roles(userRoles)
                            .build();

                        userRepository.persist(user);

                        log.info("Created user {} <{}> with ID #{}", user.getName(), user.getEmail(), user.getId());

                        return user;
                    });
            })
            .collect(Collectors.toMap(User::getName, user -> user));
    }

    @Transactional
    void createAndMapGroups(Map<String, List<String>> groupsWithUsers, Map<String, User> mappedUsers) {
        for (var entry : groupsWithUsers.entrySet()) {
            var groupName = entry.getKey();
            var users = entry.getValue();

            var group = groupRepository.findByName(groupName)
                .orElseGet(() -> {
                    var newGroup = Group.builder()
                        .name(groupName)
                        .build();

                    groupRepository.persist(newGroup);

                    log.info("Created group {} with ID #{}", newGroup.getName(), newGroup.getId());

                    return newGroup;
                });

            group.setUsers(
                mappedUsers.entrySet()
                    .stream()
                    .filter(userEntry -> users.contains(userEntry.getKey()))
                    .map(Map.Entry::getValue)
                    .toList()
            );
        }
    }
}
