package dev.roelofr.domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record UserListDto(
    Long id,
    String name,
    String email,
    DistrictDto district
) {
}
