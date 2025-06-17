package dev.roelofr.rest.request;

import dev.roelofr.rest.validation.DistrictExists;

import java.util.List;

public record ActivateUserRequest(
    List<String> roles,
    @DistrictExists Long district
) {
    //
}
