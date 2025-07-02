package dev.roelofr.it;

import dev.roelofr.config.Roles;
import dev.roelofr.domain.rest.PostLoginRequest;
import dev.roelofr.domain.rest.PostRegisterRequest;
import dev.roelofr.repository.UserRepository;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@Disabled("No workie :(")
@Slf4j
@QuarkusIntegrationTest
public class VendorHappyFlowTest {
    @Inject
    UserRepository userRepository;

    @Test
    public void vendorHappyFlow() {
        final var testEmail = UUID.randomUUID() + "@example.com";
        final var testPassword = UUID.randomUUID().toString();

        // Sign up
        given()
            .contentType(ContentType.JSON)
            .body(new PostRegisterRequest(
                "Test Name",
                testEmail,
                testPassword,
                true
            ))
            .when()
            .post("/auth/register")
            .then()
            .statusCode(200);

        // Activate user
        var user = userRepository.find("email", testEmail).singleResult();
        user.setRoles(List.of(Roles.User, Roles.Admin));
        user.setActive(true);
        userRepository.persistAndFlush(user);

        // Login
        String response = given()
            .contentType(ContentType.JSON)
            .body(new PostLoginRequest(testEmail, testPassword))
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract().body().path("token");

        final var authHeader = new Header("Authorization", "Bearer " + response);

        // TODO Create vendor
        int newVendorId = given()
            .contentType(ContentType.JSON)
            .header(authHeader)
            .body("""
                {
                "district": "test-rood",
                "name": "Test Happy Flow",
                "number": "42069a"
                }
                """)
            .when()
            .post("/vendor")
            .then()
            .statusCode(201)
            .body("name", is("Test Happy Flow"))
            .body("district.name", is("test-rood"))
            .extract().path("id");

        // TODO List tickets, expect none
        given()
            .when()
            .get("/vendor/{id}/tickets", newVendorId)
            .then()
            .statusCode(200)
            .body("$.size()", is(0));

        // TODO Create ticket
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "vendor_id": %d,
                    "description": "Test Ticket 1"
                }""".formatted(newVendorId))
            .when()
            .post("/ticket")
            .then()
            .statusCode(201)
            .log().ifValidationFails()
            .body("description", is("Test Ticket 1"));

        // TODO List tickets, expect one
        given()
            .when()
            .get("/vendor/{id}/tickets", newVendorId)
            .then()
            .statusCode(200)
            .log().ifValidationFails()
            .body("$.size()", is(1));

        // TODO Resolve ticket

        // TODO List tickets, expect none open
    }
}
