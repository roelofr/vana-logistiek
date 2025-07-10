package dev.roelofr.security;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class JwtLocalRolesAugmentor implements SecurityIdentityAugmentor {
    @Inject
    BlockingJwtRolesAgumentor jwtRolesAugmentor;

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        if (identity.isAnonymous()) {
            return Uni.createFrom().item(identity);
        }

        // Hibernate ORM is blocking
        return context.runBlocking(() -> jwtRolesAugmentor.augment(identity));
    }
}
