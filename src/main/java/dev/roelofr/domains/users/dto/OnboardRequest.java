package dev.roelofr.domains.users.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.FormParam;

import java.io.File;

public record OnboardRequest(
    @FormParam("groupId") @Positive Long groupId,
    @NotNull @FormParam("picture") File picture
) {
}
