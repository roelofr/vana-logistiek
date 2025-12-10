package dev.roelofr.domain.dto;

import dev.roelofr.domain.Team;
import dev.roelofr.domain.User;
import io.quarkus.hibernate.orm.panache.common.NestedProjectedClass;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

@Builder
@RegisterForReflection
public record UserListDto(
    Long id,
    String name,
    String email,
    List<String> roles,
    Optional<UserListTeam> team
) {
    public UserListDto(User user) {
        this(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRoles(),
            Optional.ofNullable(
                UserListTeam.ofNullable(user.getTeam())
            )
        );
    }

    @NestedProjectedClass
    public record UserListTeam(
        Long id,
        String name,
        String colour
    ) {
        static UserListTeam ofNullable(Team team) {
            if (team == null)
                return null;

            return new UserListTeam(team.getId(), team.getName(), team.getColour());
        }
    }
}
