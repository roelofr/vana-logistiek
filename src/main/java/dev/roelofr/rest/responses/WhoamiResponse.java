package dev.roelofr.rest.responses;


import dev.roelofr.domain.User;

import java.util.List;

public record WhoamiResponse(
    String name,
    String email,
    List<String> roles
) {
    public WhoamiResponse(User user) {
        this(
            user.getName(),
            user.getEmail(),
            user.getRoles()
        );
    }

}
