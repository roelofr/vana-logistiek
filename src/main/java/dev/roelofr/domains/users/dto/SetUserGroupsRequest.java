package dev.roelofr.domains.users.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record SetUserGroupsRequest(
    @NotNull List<@Positive Long> groups
) {
    //
}
