package dev.roelofr.service;

import dev.roelofr.DomainHelper;
import dev.roelofr.repository.UserRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@QuarkusTest
class AuthenticationServiceTest {
    private static final String TEST_EMAIL = "test@example.com";

    @Inject
    JWTParser jwtParser;

    @InjectMock
    UserRepository userRepository;

    @Inject
    AuthenticationService authenticationService;

    DomainHelper domainHelper = new DomainHelper();

    String getTestEmail() {
        return UUID.randomUUID() + "@example.com";
    }

    @Test
    @Disabled("VertX seems to act up")
    @TestTransaction
    void authenticateValid() {
//        final var testPassword = "password1234";
//        final var testEmail = getTestEmail();
//
//        var testUser = User.builder()
//            .name("Test User")
//            .email(testEmail)
//            .password(BcryptUtil.bcryptHash(testPassword))
//            .active(true)
//            .roles(List.of("TEST"))
//            .build();
//
//        var response = authenticationService.authenticate(
//            new ActingUser("Test", "test"),
//            testEmail,
//            testPassword
//        );
//
//        // Initial response check
//        assertNotNull(response);
//        assertTrue(response.success());
//        assertNull(response.reason());
//        assertEquals(testUser.getEmail(), response.user().getEmail());
//        assertNotNull(response.token());
//        assertNotNull(response.tokenExpiration());
//
//        var parsedJwt = assertDoesNotThrow(() -> jwtParser.parse(response.token()), "Failed to parse JWT");
//
//        assertEquals(testUser.getEmail(), parsedJwt.getClaim(Claims.upn));
//        assertEquals(testUser.getName(), parsedJwt.getSubject());
//
//        var testRoles = testUser.getRoles();
//        var claims = parsedJwt.getGroups();
//        assertEquals(testRoles.size(), claims.size(), "Claim count should match role count");
//        for (var claim : claims) {
//            assertTrue(testRoles.contains(claim), "Claim [%s] not part of user grouos".formatted(claim));
//        }
    }
}
