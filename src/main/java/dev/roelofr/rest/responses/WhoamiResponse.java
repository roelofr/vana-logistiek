package dev.roelofr.rest.responses;


import com.fasterxml.jackson.annotation.JsonInclude;
import dev.roelofr.domain.Team;
import dev.roelofr.domain.User;

import java.util.List;

public record WhoamiResponse(
    Long id,
    String name,
    String email,
    List<String> roles,
    WhoamiTeam team,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String jwt
) {
    public WhoamiResponse(User user, String jwt) {
        this(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRoles(),
            WhoamiTeam.fromNullable(user.getTeam()),
            jwt
        );
    }

    public WhoamiResponse(User user) {
        this(user, null);
    }

    record WhoamiTeam(
        Long id,
        String name,
        String icon,
        String colour
    ) {
        public WhoamiTeam(Team team) {
            this(team.getId(), team.getName(), team.getIcon(), team.getColour());
        }

        static WhoamiTeam fromNullable(Team team) {
            return team == null ? null : new WhoamiTeam(team);
        }
    }
}
