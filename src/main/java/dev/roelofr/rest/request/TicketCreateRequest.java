package dev.roelofr.rest.request;

public record TicketCreateRequest(
    long vendorId,
    String description
) {
}
