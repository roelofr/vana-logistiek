package dev.roelofr.it.users;

import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.users.UserDomainResource;
import dev.roelofr.domains.users.UserService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@QuarkusTest
@TestHTTPEndpoint(UserDomainResource.class)
class UserLookupIT {
    private static final String TEST_USER = "test-steve";

    @Inject
    UserService userService;

    User user;

    @BeforeEach
    @Transactional
    void findOrCreateTestUser() {
        user = userService.findByProviderId(TEST_USER)
            .orElseGet(() -> {
                var user = User.builder()
                    .name("Test User")
                    .providerId(TEST_USER)
                    .email(String.format("%s@example.com", UUID.randomUUID()))
                    .build();

                userService.save(user);

                return user;
            });
    }

    @Test
    @TestSecurity(user = TEST_USER)
    void findMe() {
        RestAssured.given()
            .accept(ContentType.JSON)
            .when()
            .get("/me")
            .then()
            .assertThat()
            .statusCode(200)
            .and()
            .body("id", Matchers.comparesEqualTo(user.getId()))
            .body("providerId", Matchers.comparesEqualTo(user.getProviderId()))
            .body("name", Matchers.comparesEqualTo(user.getName()))
            .body("email", Matchers.comparesEqualTo(user.getEmail()))
            .body("roles", Matchers.notNullValue())
            .body("groups", Matchers.notNullValue());
    }

    @Test
    @TestSecurity(user = TEST_USER)
    void findAll() {
        RestAssured.given()
            .accept(ContentType.JSON)
            .when()
            .get("/")
            .then()
            .assertThat()
            .statusCode(200)
            .and()

            // Expected values
            .body("id", Matchers.hasItem(user.getId()))
            .body("name", Matchers.hasItem(user.getName()))

            // Not expected values
            .body("email", Matchers.empty());
    }

    @Test
    @TestSecurity(user = TEST_USER)
    void findByIdExists() {
        RestAssured.given()
            .accept(ContentType.JSON)
            .when()
            .get("/{id}", user.getId())
            .then()
            .assertThat()
            .statusCode(200)
            .and()

            // Values expected to be present
            .body("id", Matchers.comparesEqualTo(user.getId()))
            .body("name", Matchers.comparesEqualTo(user.getName()))
            .body("groups", Matchers.notNullValue())

            // The next values should be omitted here
            .body("roles", Matchers.empty())
            .body("providerId", Matchers.empty())
            .body("email", Matchers.empty());
    }

    @Test
    @TestSecurity(user = TEST_USER)
    void findByIdNotExists() {
        Assumptions.assumeFalse(userService.findById(9000L).isPresent(), "There are OVER 9000 users!");

        RestAssured.given()
            .accept(ContentType.JSON)
            .when()
            .get("/{id}", 9000)
            .then()
            .assertThat()
            .statusCode(404)
            .and()
            .body(Matchers.empty());
    }
}
