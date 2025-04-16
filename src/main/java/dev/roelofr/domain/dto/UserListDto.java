package dev.roelofr.domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public record UserListDto(
    Long id,
    String name,
    String email,
    DistrictDto district
) {
}
