package dev.roelofr.domains.issue.dto;

import dev.roelofr.support.validation.VendorExists;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateIssueRequest(
    @NotNull @VendorExists long vendorId,
    @NotNull @NotBlank @Length(min = 2, max = 200) String title
) {
}
