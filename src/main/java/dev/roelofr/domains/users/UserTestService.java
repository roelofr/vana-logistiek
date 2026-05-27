package dev.roelofr.domains.users;

import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.GroupRepository;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.users.model.UserRepository;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.LaunchMode;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@IfBuildProfile("test")
@RequiredArgsConstructor
public class UserTestService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @PostConstruct
    void throwIfNonTest() {
        if (!LaunchMode.current().equals(LaunchMode.TEST))
            throw new IllegalStateException("Cannot use UserTestService in production");
    }

    /**
     * Finds a test user
     *
     * @throws jakarta.persistence.NoResultException        if no user is found
     * @throws jakarta.persistence.NonUniqueResultException if multiple test users are found with the same name
     */
    public @Nonnull User findTestUser(@NotBlank String name) {
        return userRepository.find("""
            (LOWER(name) = LOWER(?1)
            AND email LIKE '%@example.com')
            OR (email = CONCAT(LOWER(?1), '@example.com'))
            """, name).singleResult();
    }

    /**
     * Finds a test group
     *
     * @throws jakarta.persistence.NoResultException        if no group is found
     * @throws jakarta.persistence.NonUniqueResultException if multiple test groups are found with the same name
     */
    public @Nonnull Group findTestGroup(@NotBlank String name) {
        return groupRepository.find("LOWER(name) = LOWER(?1) AND label LIKE 'test-%' AND system = true", name).singleResult();
    }

}
