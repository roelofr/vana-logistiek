package dev.roelofr.security;

import dev.roelofr.domain.User;
import dev.roelofr.service.JwtSubjectUserCache;
import dev.roelofr.service.UserService;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Slf4j
@ApplicationScoped
public class BlockingJwtRolesAgumentor {
    @Inject
    UserService userService;

    @Inject
    JwtSubjectUserCache jwtSubjectUserCache;

    @Inject
    LaunchMode launchMode;

    @ActivateRequestContext
    SecurityIdentity augment(SecurityIdentity identity) {
        if (identity.getPrincipal() instanceof JsonWebToken)
            return augmentJwt(identity);

        if (launchMode != LaunchMode.TEST)
            return identity;

        log.warn("####################################");
        log.warn("###                              ###");
        log.warn("###       !!  WARNING  !!        ###");
        log.warn("### Using test-mode augmentation ###");
        log.warn("###                              ###");
        log.warn("####################################");
        return augmentTestMode(identity);
    }

    private SecurityIdentity augmentTestMode(SecurityIdentity identity) {
        var userOptional = userService.findByEmail(identity.getPrincipal().getName());

        if (userOptional.isEmpty())
            return identity;

        var user = userOptional.get();
        if (!user.getEmail().endsWith("@example.com"))
            throw new RuntimeException("Non-test email address used for augmentation!");

        return copyAndAddRoles(identity, user.getRoles());
    }

    private SecurityIdentity augmentJwt(SecurityIdentity identity) {
        // Unsafe cast is okay here
        JsonWebToken jwt = (JsonWebToken) identity.getPrincipal();

        // Get the user from the JWT ID
        var user = getUserFromJwt(jwt);
        if (user == null) {
            return identity;
        }

        log.info("Adding roles {} to JWT {}", user.getRoles(), jwt.getSubject());

        return copyAndAddRoles(identity, user.getRoles());
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

    private SecurityIdentity copyAndAddRoles(SecurityIdentity identity, List<String> roles) {
        var builder = QuarkusSecurityIdentity.builder(identity);

        roles.forEach(builder::addRole);

        return builder.build();
    }
}
