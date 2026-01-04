package dev.roelofr.jobs;

import dev.roelofr.config.Roles;
import dev.roelofr.repository.UserRepository;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.Startup;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NonUniqueResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PromoteUsersOnBoot {
    final UserRepository userRepository;
    final LaunchMode launchMode;

    @Startup
    @Transactional
    @Priority(Priorities.Develop)
    void promoteOnStartup() {
        if (launchMode.equals(LaunchMode.TEST))
            return;

        promoteUsersMissingUserRole();
        promoteInitialUserToAdmin();
    }

    @Transactional
    void promoteUsersMissingUserRole() {
        userRepository.stream("roles is not null")
            .forEach(user -> {
                if (user.getRoles() == null)
                    return;

                if (user.getRoles().isEmpty())
                    return;

                if (user.getRoles().contains(Roles.User))
                    return;

                log.info("Add user-role to user {}: {}", user.getId(), user.getName());

                var userRoles = new ArrayList<>(user.getRoles());
                userRoles.add(Roles.User);
                user.setRoles(userRoles);
            });
    }

    @Transactional
    void promoteInitialUserToAdmin() {
        if (userRepository.count() != 1) {
            log.info("Not running promoteInitialUserToAdmin, userRepository count is {}", userRepository.count());
            return;
        }

        this.assignAdminToFirstUser();
    }

    void assignAdminToFirstUser() {
        try {
            var user = userRepository.findAll().singleResult();

            user.setRoles(List.of(
                Roles.User,
                Roles.Admin
            ));

            log.info("Activated user {} and assigned admin role.", user.getEmail());
        } catch (jakarta.persistence.NoResultException exception) {
            log.error("Admin assignment was triggered, but no users exist.");
        } catch (NonUniqueResultException e) {
            log.error("Admin assignment was triggered, but multiple users exist.");
        }
    }
}
