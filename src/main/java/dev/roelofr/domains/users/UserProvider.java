package dev.roelofr.domains.users;

import dev.roelofr.domains.users.model.User;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.ws.rs.core.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
@RequiredArgsConstructor
public class UserProvider {
    private final UserService userService;

    @PostConstruct
    void reportConstruction() {
        log.info("User Provider was constructed");
    }

    @Context
    @Produces
    User getUser(@Context SecurityIdentity identity) {
        log.info("User Provider was asked for a USER USER USER USER USER USER USER ");

        return userService.findByPrincipal(identity.getPrincipal())
            .orElse(null);
    }
}
