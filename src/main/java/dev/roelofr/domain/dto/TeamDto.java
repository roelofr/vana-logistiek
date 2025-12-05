package dev.roelofr.domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
