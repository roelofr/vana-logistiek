package dev.roelofr.rest.resources;

import dev.roelofr.config.Roles;
import dev.roelofr.repository.UserRepository;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static dev.roelofr.DomainHelper.EMAIL_USER;
import static io.restassured.RestAssured.with;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    @TestSecurity(user = EMAIL_USER, roles = {Roles.User})
    void testUnderprivileged() {
        with()
            .contentType(ContentType.BINARY)
            .body(InputStream.nullInputStream())
            .when()
            .post("/import-vendors")
            .then()
            .onFailMessage("UserResource::importVendorList")
            .statusCode(403);

        with()
            .contentType(ContentType.JSON)
            .body("{}")
            .post("/users/1/activate")
            .then()
            .onFailMessage("UserResource::activateUser")
            .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin@example.com", roles = {Roles.Admin})
    void activateUser() {
        var userOptional = userRepository.findByEmailOptional("frozen-for-activation@example.com");
        assumeTrue(userOptional.isPresent());
        var user = userOptional.get();

        assertFalse(user.isActive());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());

//        with()
//            .contentType(ContentType.JSON)
//            .body(ActivateUserRequest.builder()
//                .roles(List.of(Roles.User))
//                .build()
//            )
//            .when()
//            .post("/users/{id}/activate", user.getId())
//            .then()
//            .assertThat()
//            .statusCode(200)
//
//            .and()
//            .body("id", equalTo(user.getId().intValue()))
//            .body("name", equalTo(user.getName()))
//            .body("active", equalTo(true))
//            .body("roles", equalTo(List.of(Roles.User)));
    }
}
