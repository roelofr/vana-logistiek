package dev.roelofr.domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

import java.util.List;

@Builder
@RegisterForReflection
public record UserDto(
    Long id,
    String name,
    String email,
    List<String> roles,
    TeamDto district
) {
}
