package dev.roelofr.rest.dtos;

import dev.roelofr.domain.Team;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public record EmbeddedDistrict(
    long id,
    String name,
    String mobileName,
    String colour
) {
    public EmbeddedDistrict(@Nonnull Team team) {
        this(
            team.getId(),
            team.getName(),
            team.getMobileName(),
            team.getColour()
        );
    }

    public static EmbeddedDistrict fromNullable(@Nullable Team team) {
        if (team == null)
            return null;

        return new EmbeddedDistrict(team);
    }
}
