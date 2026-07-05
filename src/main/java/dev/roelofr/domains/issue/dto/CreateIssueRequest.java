package dev.roelofr.domains.issue.dto;

import dev.roelofr.domain.dto.Location;
import dev.roelofr.support.validation.VendorExists;
import dev.roelofr.validation.ValidatedItself;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateIssueRequest(
    @NotNull @NotBlank @Length(min = 2, max = 200) String title,
    @VendorExists Long vendorId,
    @ValidatedItself Location location
) {
    //
}
