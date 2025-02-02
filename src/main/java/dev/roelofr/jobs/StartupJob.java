package dev.roelofr.jobs;

import dev.roelofr.repository.UserRepository;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.VertxContextSupport;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class StartupJob {
    @Inject
    UserRepository userRepository;

    public void startupEventListener(@Observes StartupEvent event) {
        VertxContextSupport.subscribe(
            () -> Multi.createFrom().uni(loadUsers()),
            (x) -> log.info("User loaded: " + x)
        );
    }

    @Transactional
    public Uni<Void> loadUsers() {
        return Uni.createFrom()
            .nothing()
            .invoke(() -> log.info("Starting data migration"))

            // reset and load all test users
            .chain(() -> userRepository.deleteAll())
            .invoke(() -> log.info("Users deleted"))

            // Add test users
            .chain(() -> userRepository.addUser("admin", "admin", "admin"))
            .chain(() -> userRepository.addUser("user", "user", "user"))
            .chain(() -> userRepository.addUser("guest", "guest", ""))
            .invoke(() -> log.info("Users added"))

            // No returns
            .replaceWithVoid();
    }
}
