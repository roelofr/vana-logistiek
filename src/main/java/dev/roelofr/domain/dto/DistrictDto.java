package dev.roelofr.domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RegisterForReflection
@RequiredArgsConstructor
public class DistrictDto {
    private final Long id;
    private final String name;
    private final String mobileName;
    private final String colour;
    //
}
