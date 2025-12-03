package dev.roelofr.rest.dtos;

import dev.roelofr.domain.Team;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record DistrictHttpDto(long id, String name, String color, String icon) {
    public DistrictHttpDto(@Nonnull Team team) {
        this(team.getId(), team.getName(), team.getColour(), team.getIcon());
    }

    public static DistrictHttpDto fromNullable(@Nullable Team team) {
        if (team == null)
            return null;

        return new DistrictHttpDto(team);
    }
}
