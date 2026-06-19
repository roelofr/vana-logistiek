package dev.roelofr.security;

import dev.roelofr.domains.users.UserService;
import dev.roelofr.domains.users.model.GroupRepository;
import dev.roelofr.domains.users.model.User;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Slf4j
@RequestScoped
@RequiredArgsConstructor
public class UserProvider {
    public static final String GROUP_CLAIM = "pla/group";

    private final UserService userService;
    private final GroupRepository groupRepository;

    @Context
    @Produces
    User getContextUser(@Context SecurityIdentity securityIdentity) {
        var foundUser = userService.findWithRelationsByPrincipal(securityIdentity.getPrincipal()).orElse(null);
        if (foundUser != null) {
            log.info("Supplying @Context User {}", foundUser);
            return foundUser;
        }

        if (securityIdentity.getPrincipal() instanceof JsonWebToken jwtPrincipal) {
            var emailUser = findAndLinkByEmail(jwtPrincipal);
            if (emailUser != null) {
                log.info("UserProvider has LINKED an existing user FROM JWT");
                return emailUser;
            }

            log.info("UserProvider is CREATING a new user FROM JWT");
            return createMissingUser(jwtPrincipal);
        }

        log.info("USER FAILED TO PROVISION!");

        return null;
    }

    @Transactional
    User findAndLinkByEmail(JsonWebToken jwt) {
        var subject = jwt.getSubject();

        var email = jwt.getClaim(Claims.email);
        if (email == null)
            return null;

        var userOptional = userService.findByEmail(jwt.getClaim(Claims.email));
        if (userOptional.isEmpty())
            return null;

        var user = userOptional.get();
        if (user.getProviderId() != null && !subject.equals(user.getProviderId()))
            throw new ForbiddenException("User already exists with a different provider ID");

        user.setProviderId(subject);
        return user;
    }

    @Transactional
    User createMissingUser(JsonWebToken jwt) {
        var newUser = User.builder()
            .name(jwt.getName())
            .roles(jwt.getGroups().stream().toList())
            .providerId(jwt.getSubject())
            .email(jwt.getClaim(Claims.email))
            .build();

        userService.save(newUser);

        var groupClaim = jwt.<String>getClaim(GROUP_CLAIM);
        if (groupClaim != null)
            groupRepository.findByName(groupClaim)
                .ifPresent(group -> newUser.setGroups(List.of(group)));

        return newUser;
    }
}

