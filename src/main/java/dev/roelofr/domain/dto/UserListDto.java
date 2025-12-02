package dev.roelofr.domain.dto;

import dev.roelofr.domain.User;
import io.quarkus.hibernate.orm.panache.common.NestedProjectedClass;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
import java.util.Optional;

@RegisterForReflection
public record UserListDto(
    Long id,
    String name,
    String email,
    boolean active,
    List<String> roles,
    Optional<UserListDistrict> district
) {
    public UserListDto(User user) {
        this(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.isActive(),
            user.getRoles(),
            Optional.ofNullable(
                user.getTeam() == null ? null : new UserListDistrict(
                    user.getTeam().getId(),
                    user.getTeam().getName(),
                    user.getTeam().getColour()
                )
            )
        );
    }

    @NestedProjectedClass
    public record UserListDistrict(
        Long id,
        String name,
        String colour
    ) {
        //
    }
}
