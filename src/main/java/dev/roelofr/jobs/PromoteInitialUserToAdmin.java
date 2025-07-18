package dev.roelofr.jobs;

import dev.roelofr.config.Roles;
import dev.roelofr.repository.UserRepository;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.persistence.NonUniqueResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PromoteInitialUserToAdmin {
    final UserRepository userRepository;
    final LaunchMode launchMode;

    void promoteOnStartup(@Observes StartupEvent startupEvent) {
        if (launchMode.equals(LaunchMode.TEST))
            return;

        promoteInitialUserToAdmin();
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
            user.setActive(true);

            log.info("Activated user {} and assigned admin role.", user.getEmail());
        } catch (jakarta.persistence.NoResultException exception) {
            log.error("Admin assignment was triggered, but no users exist.");
        } catch (NonUniqueResultException e) {
            log.error("Admin assignment was triggered, but multiple users exist.");
        }
    }
}
