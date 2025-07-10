package dev.roelofr.rest.responses;

import dev.roelofr.domain.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Set;

public record AuthMeResponse(
    String subject,
    String keyId,
    User user,
    Set<String> roles
) {
    public AuthMeResponse(JsonWebToken jwt, User user) {
        this(
            jwt.getSubject(),
            jwt.getTokenID(),
            user,
            jwt.getGroups()
        );
    }
}
