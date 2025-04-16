package dev.roelofr.domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public record UserDto(
    Long id,
    String name,
    String email,
    List<String> roles,
    DistrictDto district
) {
}
