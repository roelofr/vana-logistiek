package dev.roelofr.rest.responses;


import com.fasterxml.jackson.annotation.JsonInclude;
import dev.roelofr.domain.User;

import java.util.List;

public record WhoamiResponse(
    String name,
    String email,
    List<String> roles,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String jwt
) {
    public WhoamiResponse(User user, String jwt) {
        this(
            user.getName(),
            user.getEmail(),
            user.getRoles(),
            null
        );
    }

    public WhoamiResponse(User user) {
        this(user, null);
    }
}
