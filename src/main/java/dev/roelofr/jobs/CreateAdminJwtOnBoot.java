package dev.roelofr.jobs;

import com.github.javafaker.Faker;
import dev.roelofr.repository.UserRepository;
import dev.roelofr.service.AuthenticationService;
import io.quarkus.panache.common.Parameters;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static dev.roelofr.Constants.DEFAULT_ADMIN_EMAIL;

@Slf4j
@ApplicationScoped
public class CreateAdminJwtOnBoot {

    @Inject
    UserRepository userRepository;

    @Inject
    AuthenticationService authService;

    @Inject
    LaunchMode launchMode;

    Faker faker;

    void createAdminJwtOnBoot(@Observes StartupEvent startupEvent) {
        if (launchMode.equals(LaunchMode.DEVELOPMENT))
            this.createAdminJwt();
    }

    void createAdminJwt() {
        var adminUser = userRepository.find("email = :email AND active = true", Parameters.with("email", DEFAULT_ADMIN_EMAIL)).singleResultOptional();

        if (adminUser.isEmpty()) {
            log.info("No enabled admin user present, not issuing JWT");
            return;
        }

        var expiration = Instant.now().plus(7, ChronoUnit.DAYS);

        log.info("Admin JWT valid thru {}: {}", expiration, authService.buildJwt(adminUser.get(), expiration));
    }
}
