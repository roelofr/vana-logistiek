package dev.roelofr.rest.dtos;

import dev.roelofr.domain.Team;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public record EmbeddedTeam(
    long id,
    String name,
    String colour,
    String icon
) {
    public EmbeddedTeam(@Nonnull Team team) {
        this(
            team.getId(),
            team.getName(),
            team.getColour(),
            team.getIcon()
        );
    }

    public static EmbeddedTeam fromNullable(@Nullable Team team) {
        if (team == null)
            return null;

        return new EmbeddedTeam(team);
    }
}
