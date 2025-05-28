
package dev.roelofr.domain.rest;

import lombok.Builder;

@Builder
public record PostLoginRequest(
    String username,
    String password
) {
    //
}
