package dev.roelofr.domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record DistrictDto(
    Long id,
    String name,
    String mobileName,
    String colour
) {
}
