package dev.roelofr.domains.issue.dto;

import dev.roelofr.support.validation.VendorExists;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record CreateIssueDto(
    @NotBlank @Length(min = 2, max = 200)
    String subject,

    @Length(min = 1, max = 5000)
    String firstMessage,

    @NotNull
    @VendorExists
    long vendorId
) {
    //
}
