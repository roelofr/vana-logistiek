package dev.roelofr.domains.users.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record GroupAdminRequest(
    @NotBlank @Length(min = 3, max = 100) String name,
    @Length(min = 3, max = 50) String icon,
    @Length(min = 3, max = 50) String colour
) {
}
