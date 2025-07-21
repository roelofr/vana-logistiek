package dev.roelofr.rest.request;

import dev.roelofr.rest.validation.DistrictExists;
import lombok.Builder;

import java.util.List;

@Builder
public record ActivateUserRequest(
    String name,
    List<String> roles,
    @DistrictExists Long district
) {
    //
}
