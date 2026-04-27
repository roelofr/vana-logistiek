package dev.roelofr.domains.users;

import dev.roelofr.domains.users.model.User;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.Context;
import lombok.RequiredArgsConstructor;

@Dependent
@RequiredArgsConstructor
public class UserProvider {
    private final UserService userService;

    @RequestScoped
    User getUser(@Context SecurityIdentity identity) {
        return userService.findByPrincipal(identity.getPrincipal())
            .orElse(null);
    }
}
