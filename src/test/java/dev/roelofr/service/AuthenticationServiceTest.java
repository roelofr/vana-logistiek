package dev.roelofr.service;

import dev.roelofr.domain.User;
import dev.roelofr.repository.UserRepository;
import dev.roelofr.service.AuthenticationService.ActingUser;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class AuthenticationServiceTest {
    private final String testEmail = "test-auth-service@example.com";
    private final String testPassword = "RandomPassword664";

    private User testUser;

    @Inject
    JWTParser jwtParser;

    @Inject
    UserRepository userRepository;

    @Inject
    AuthenticationService authenticationService;

    @BeforeEach
    void setUpTestUser() {
        userRepository.delete("email = ?1", testEmail);

        testUser = User.builder()
            .name("Test User")
            .email(testEmail)
            .password(testPassword)
            .active(true)
            .roles(List.of("TEST"))
            .build();

        userRepository.persist(testUser);
    }

    @Test
    void authenticateValid() {
        var response = authenticationService.authenticate(
            new ActingUser("Test", "test"),
            testEmail,
            testPassword
        );

        // Initial response check
        assertNotNull(response);
        assertTrue(response.success());
        assertNull(response.reason());
        assertEquals(testUser.getEmail(), response.username());
        assertNotNull(response.token());
        assertNotNull(response.tokenExpiration());

        var parsedJwt = assertDoesNotThrow(() -> jwtParser.parse(response.token()), "Failed to parse JWT");

        assertEquals(testUser.getEmail(), parsedJwt.getClaim(Claims.upn));
        assertEquals(testUser.getName(), parsedJwt.getSubject());

        var testRoles = testUser.getRoles();
        var claims = parsedJwt.getGroups();
        assertEquals(testRoles.size(), claims.size(), "Claim count should match role count");
        for (var claim : claims) {
            assertTrue(testRoles.contains(claim), "Claim [%s] not part of user grouos".formatted(claim));
        }
    }
}
