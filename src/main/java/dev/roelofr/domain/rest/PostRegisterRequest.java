
package dev.roelofr.domain.rest;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.hibernate.validator.constraints.Length;

@Builder
public record PostRegisterRequest(
    @NotBlank @Length(min = 1, max = 200) String name,
    @NotBlank @Email String email,
    @NotBlank @Length(min = 8) String password,
    @NotNull @AssertTrue Boolean acceptTerms
) {
    //
}
