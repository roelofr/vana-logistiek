package dev.roelofr.rest.resources;

import dev.roelofr.config.Roles;
import dev.roelofr.domain.User;
import dev.roelofr.repository.UserRepository;
import dev.roelofr.rest.request.ActivateUserRequest;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Slf4j
@QuarkusTest
@TestHTTPEndpoint(AdminResource.class)
class AdminResourceTest {
    @Inject
    UserRepository userRepository;

    @Test
//    @TestTransaction
    @TestSecurity(user = "admin@example.com", roles = {Roles.Admin})
    void activateUser() {
        var userOptional = userRepository.findByEmailOptional("mr-freeze@example.com");
        assumeTrue(userOptional.isPresent());
        var user = userOptional.get();

        assertFalse(user.isActive());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());

        given()
            .contentType(ContentType.JSON)
            .body(ActivateUserRequest.builder()
                .roles(List.of(Roles.User))
                .build()
            )
            .when()
            .post("/users/{id}/activate", user.getId())
            .then()
            .assertThat()
            .statusCode(200)

            .and()
            .body("id", equalTo(user.getId().intValue()))
            .body("name", equalTo(user.getName()))
            .body("active", equalTo(true))
            .body("roles", equalTo(List.of(Roles.User)));
    }
}
