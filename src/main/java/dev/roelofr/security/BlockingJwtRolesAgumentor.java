package dev.roelofr.security;

import dev.roelofr.domain.User;
import dev.roelofr.service.JwtSubjectUserCache;
import dev.roelofr.service.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Slf4j
@ApplicationScoped
public class BlockingJwtRolesAgumentor {
    @Inject
    UserService userService;

    @Inject
    JwtSubjectUserCache jwtSubjectUserCache;

    @ActivateRequestContext
    SecurityIdentity augment(SecurityIdentity identity) {
        if (!(identity.getPrincipal() instanceof JsonWebToken jwt)) {
            return identity;
        }

        // Get the user from the JWT ID
        var user = getUserFromJwt(jwt);
        if (user == null) {
            return identity;
        }

        // create a new builder and copy principal, attributes, credentials and roles from the original identity
        var builder = QuarkusSecurityIdentity.builder(identity);

        // add custom roles
        user.getRoles().forEach(builder::addRole);

        log.info("Added roles {} to JWT {}", user.getRoles(), jwt.getSubject());

        // Return a builder
        return builder.build();
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
}
