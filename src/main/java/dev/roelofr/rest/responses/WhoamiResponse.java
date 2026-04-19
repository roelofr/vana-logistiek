package dev.roelofr.rest.responses;


import com.fasterxml.jackson.annotation.JsonInclude;
import dev.roelofr.domains.users.Group;
import dev.roelofr.domains.users.User;

import java.util.List;

public record WhoamiResponse(
    Long id,
    String name,
    String email,
    List<String> roles,
    List<WhoamiGroup> team,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String jwt
) {
    public WhoamiResponse(User user, String jwt) {
        this(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRoles(),
            WhoamiGroup.fromUser(user),
            jwt
        );
    }

    public WhoamiResponse(User user) {
        this(user, null);
    }

    record WhoamiGroup(
        Long id,
        String name,
        String icon,
        String colour
    ) {
        public WhoamiGroup(Group group) {
            this(group.getId(), group.getName(), null, null);
        }

        static List<WhoamiGroup> fromUser(User user) {
            var groups = user.getGroups();
            if (groups == null)
                return List.of();

            return groups.stream().map(WhoamiGroup::new).toList();
        }
    }
}
