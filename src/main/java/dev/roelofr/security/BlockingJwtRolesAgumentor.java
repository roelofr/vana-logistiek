package dev.roelofr.security;

import dev.roelofr.config.AppConfig;
import dev.roelofr.domains.users.UserService;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.service.JwtSubjectUserCache;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@ApplicationScoped
public class BlockingJwtRolesAgumentor {
    private static final String TYPE_ID_TOKEN = "id-token";

    @Inject
    UserService userService;

    @Inject
    JwtSubjectUserCache jwtSubjectUserCache;

    @Inject
    AppConfig appConfig;

    @ActivateRequestContext
    SecurityIdentity augment(SecurityIdentity identity) {
        if (identity.getPrincipal() instanceof JsonWebToken jwt)
            return augmentJwt(identity, jwt);

        return identity;
    }

    SecurityIdentity augmentJwt(SecurityIdentity identity, JsonWebToken jwt) {
        // Handle ID and access tokens differently
        var user = (TYPE_ID_TOKEN.equals(jwt.getClaim("type")))
            ? getOrCreateUserFromJwt(jwt) : getUserFromJwt(jwt);

        if (user == null)
            return identity;

        return addRolesToIdentity(identity, user.getRoles());
    }

    @Transactional
    User getOrCreateUserFromJwt(JsonWebToken jwt) {
        var existingUser = getUserFromJwt(jwt);
        var wantedGroups = jwt.getGroups();

        if (existingUser != null) {
            existingUser.getRoles().addAll(wantedGroups);
            return existingUser;
        }

        var newUser = User.builder()
            .providerId(jwt.getSubject())
            .name(getNameFromJwt(jwt))
            .roles(new HashSet<>(wantedGroups))
            .build();

        if (jwt.getClaim(Claims.email) != null) {
            var userByEmail = userService.findByEmail(jwt.getClaim(Claims.email)).orElse(null);
            if (userByEmail != null) {
                log.info("LINKING an existing user {} FROM JWT user {}", userByEmail.getName(), jwt.getSubject());
                userByEmail.setProviderId(jwt.getSubject());
                userByEmail.getRoles().addAll(wantedGroups);

                return userByEmail;
            }

            newUser.setEmail(jwt.getClaim(Claims.email));
        } else {
            newUser.setEmail(String.format("%s@users.login.troela.fun", jwt.getSubject()));
        }

        log.info("CREATING an new user {} FROM JWT user {}", newUser.getName(), jwt.getSubject());

        userService.save(newUser);

        return newUser;
    }

    private User getUserFromJwt(JsonWebToken jwt) {
        var cachedUserOpt = jwtSubjectUserCache.get(jwt);
        if (cachedUserOpt.isPresent())
            return cachedUserOpt.get();

        var providerIdUserOpt = userService.findByProviderId(jwt.getSubject());
        if (providerIdUserOpt.isEmpty())
            return null;

        var providerIdUser = providerIdUserOpt.get();
        jwtSubjectUserCache.put(jwt, providerIdUser);
        return providerIdUser;
    }

    String getNameFromJwt(JsonWebToken jwt) {
        if (jwt.getClaim(Claims.full_name) != null)
            return jwt.getClaim(Claims.full_name).toString();

        var composedName = Stream.of(
                jwt.getClaim(Claims.given_name),
                jwt.getClaim(Claims.middle_name),
                jwt.getClaim(Claims.family_name))
            .filter(Objects::nonNull)
            .map(String::valueOf)
            .filter(row -> !row.isBlank())
            .collect(Collectors.joining(" "));

        if (!composedName.isBlank())
            return composedName;

        var email = jwt.getClaim(Claims.email);
        if (email != null) {
            var nameFromEmail = ((String) email).split("@")[0]
                .toLowerCase()
                .replaceAll("[^a-z0-9]+([a-z])", " $1")
                .trim();

            if (!nameFromEmail.isBlank())
                return WordUtils.capitalize(nameFromEmail);
        }

        if (jwt.getSubject() != null)
            return jwt.getSubject();

        return jwt.getName();
    }

    private SecurityIdentity addRolesToIdentity(SecurityIdentity identity, Collection<String> roles) {
        var builder = QuarkusSecurityIdentity.builder(identity);

        roles.forEach(builder::addRole);

        if (hasUserRole(roles))
            builder.addRole(appConfig.roles().user());

        return builder.build();
    }

    private boolean hasUserRole(Collection<String> roles) {
        if (roles.contains(appConfig.roles().user()))
            return false; // Prevent duplicates

        return roles.contains(appConfig.roles().admin())
            || roles.contains(appConfig.roles().wijkhouder())
            || roles.contains(appConfig.roles().centralePost());
    }
}
