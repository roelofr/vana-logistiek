package dev.roelofr.rest.responses;

import dev.roelofr.domain.District;
import dev.roelofr.domain.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.Set;

public record AuthMeResponse(
    String subject,
    String keyId,
    EmbeddedUser user,
    Set<String> roles
) {
    public AuthMeResponse(JsonWebToken jwt, User user) {
        this(
            jwt.getSubject(),
            jwt.getTokenID(),
            new EmbeddedUser(user),
            jwt.getGroups()
        );
    }

    record EmbeddedUser(
        Long id,
        String name,
        String email,
        List<String> roles,
        District district
    ) {
        EmbeddedUser(User user) {
            this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles(),
                user.getDistrict()
            );
        }
    }
}
