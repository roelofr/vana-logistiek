package dev.roelofr.rest.responses;


import java.util.List;

public record WhoamiResponse(
    String name,
    String email,
    List<String> roles
) {

}
