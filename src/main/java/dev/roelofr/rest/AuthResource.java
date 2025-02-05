package dev.roelofr.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Path("/auth")
public class AuthResource {
    private static String hashFromEmailAndPassword(String email, String password) {
        final MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        var stringToHash = email + "::" + password;
        var hashed = digest.digest(stringToHash.getBytes(StandardCharsets.UTF_8));

        return new String(hashed);
    }

    @POST
    @Path("/login")
    @Consumes("application/json")
    @Produces("application/json")
    public LoginResponseDto login(LoginRequestDto dto) {
        return new LoginResponseDto(
            hashFromEmailAndPassword(
                dto.email(),
                dto.password()
            )
        );
    }

    public record LoginRequestDto(String email, String password) {
    }

    public record LoginResponseDto(String token) {
    }
}
