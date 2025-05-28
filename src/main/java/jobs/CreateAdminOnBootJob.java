package jobs;

import dev.roelofr.domain.User;
import dev.roelofr.repository.UserRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class CreateAdminOnBootJob {
    @Inject
    UserRepository userRepository;

    void createAdminOnBoot(@Observes StartupEvent startupEvent) {
            if (userRepository.count() == 0) {
                this.createAdmin();
            }
    }

    void createAdmin() {
        var adminUser = User.builder()
            .name("Admin user")
            .email("admin@example.com")
            .password("AdminUser123")
            .active(true)
            .build();

        userRepository.persistAndFlush(adminUser);

        log.info("Created admin user with e-mail {} and password {}", adminUser.getEmail(), adminUser.getPassword());
    }
}
