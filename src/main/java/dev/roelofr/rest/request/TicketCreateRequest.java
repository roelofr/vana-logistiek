package dev.roelofr.rest.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public record TicketCreateRequest(
    @JsonAlias("vendor_id")
    long vendorId,
    String description
) {
}
