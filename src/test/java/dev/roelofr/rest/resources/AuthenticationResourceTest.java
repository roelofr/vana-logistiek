package dev.roelofr.rest.resources;

import dev.roelofr.domain.rest.PostRegisterRequest;
import dev.roelofr.repository.UserRepository;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dev.roelofr.DomainHelper.EMAIL_USER;
import static io.restassured.RestAssured.when;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@QuarkusTest
@TestHTTPEndpoint(AuthenticationResource.class)
class AuthenticationResourceTest {
    final String TEST_EMAIL = "auth-resource-test@example.com";

    @Inject
    UserRepository userRepository;

    @Test
    @TestSecurity(user = TEST_EMAIL)
    void getMe() {
        var user = userRepository.findByEmailOptional(TEST_EMAIL).orElseThrow();
        userRepository.persistAndFlush(user);

        when().get("/me")
            .then()
            .statusCode(200)

            .assertThat()
            .body("id", comparesEqualTo(user.getId()))
            .body("name", equalTo(user.getName()))
            .body("email", equalTo(TEST_EMAIL));
    }

    @Test
    void postLoginHappyTrail() {
        with()
            .contentType(ContentType.JSON)
            .body("""
                {
                  "username": "%s",
                  "password": "testtest"
                }
                """.formatted(EMAIL_USER))
            .post("/login")
            .then()
            .statusCode(200)
            .assertThat()
            .body(".name", equalTo(TEST_EMAIL));
    }

    @Test
    void postRegister() {
        var testEmail = UUID.randomUUID() + "@example.com";
        var testPassword = UUID.randomUUID().toString();

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(new PostRegisterRequest("Test", testEmail, testPassword, true))
            .when()
            .post("/register")
            .then()
            .statusCode(201);

        var userOptional = userRepository.findByEmailOptional(testEmail);
        assertTrue(userOptional.isPresent(), "User is not present");

        var user = userOptional.get();
        assertFalse(user.isActive(), "User must not be active");
        assertTrue(user.getRoles().isEmpty(), "User must not have roles");
    }
}
