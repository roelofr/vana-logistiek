package dev.roelofr.jobs;

import dev.roelofr.domain.User;
import dev.roelofr.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class CreateAdminOnBootJob {
    private final String adminUsername = "admin@example.com";
    private final String adminPassword = "AdminUser123";

    @Inject
    UserRepository userRepository;

    @Transactional
    void createAdminOnBoot(@Observes StartupEvent startupEvent) {
        if (userRepository.count() == 0) {
            this.createAdmin();
        } else {
            this.updateAdmin();
        }
    }

    void createAdmin() {
        var adminUser = User.builder()
            .name("Admin user")
            .email(adminUsername)
            .password(BcryptUtil.bcryptHash(adminPassword))
            .active(true)
            .build();

        userRepository.persistAndFlush(adminUser);

        log.info("Created admin user with e-mail {} and password {}", adminUsername, adminPassword);
    }

    void updateAdmin() {
        var adminUser = userRepository.findByEmailOptional(adminUsername);

        if (adminUser.isEmpty())
            return;

        var user = adminUser.get();
        user.setPassword(BcryptUtil.bcryptHash(adminPassword));
        user.setRoles(List.of());
        userRepository.persistAndFlush(user);

        log.info("Reset admin password for user {}", adminUsername);
    }
}
