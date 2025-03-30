package dev.roelofr.rest.request;

public record VendorCreateRequest(
    String district,
    String number,
    String name
) {
}
