package dev.roelofr.rest.dtos;

import dev.roelofr.domain.Team;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record DistrictHttpDto(long id, String name, String mobileName, String colour) {
    public DistrictHttpDto(@Nonnull Team team) {
        this(team.getId(), team.getName(), team.getMobileName(), team.getColour());
    }

    public static DistrictHttpDto fromNullable(@Nullable Team team) {
        if (team == null)
            return null;

        return new DistrictHttpDto(team);
    }
}
