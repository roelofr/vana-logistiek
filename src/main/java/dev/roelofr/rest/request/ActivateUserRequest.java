package dev.roelofr.rest.request;

import dev.roelofr.Constants;
import dev.roelofr.rest.validation.DistrictExists;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Optional;

public record ActivateUserRequest(
    List<String> roles,
    @DistrictExists Long district
) {
    //
}
