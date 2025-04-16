package dev.roelofr.rest.responses;


import java.time.LocalDateTime;

public record LoginResponse(String name, String jwt, LocalDateTime expiration) {

}
