
package dev.roelofr.domain.rest;

import lombok.Builder;

@Builder
public record PostRegisterRequest(
    String name,
    String email,
    String password,
    Boolean acceptTerms
) {
    //
}
