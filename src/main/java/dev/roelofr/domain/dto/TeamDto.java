package dev.roelofr.domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

@Builder
@RegisterForReflection
public record TeamDto(
    Long id,
    String name,
    String colour,
    String icon
) {
    //
}
