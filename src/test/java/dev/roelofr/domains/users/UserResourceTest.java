package dev.roelofr.domains.users;

import dev.roelofr.domains.users.model.UserFlags;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

@QuarkusTest
@TestHTTPEndpoint(UserResource.class)
class UserResourceTest {
    @Inject
    UserService userService;

    @Test
    @TestSecurity(user = "bob")
    void findMe() {
        RestAssured.given()
            .when()
            .get("/me")
            .then()
            .statusCode(200)
            .and()
            .body("providerId", Matchers.equalTo("bob"))
            .body("flags", Matchers.empty());
    }

    @Test
    @TestSecurity(user = "bob")
    void onboardMe() {
        QuarkusTransaction.requiringNew().run(() -> {
            var user = userService.findByProviderId("bob").orElseThrow();
            user.setFlags(new ArrayList<>());
        });

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "groupId": null
                }
                """)
            .when()
            .post("/me/onboard")
            .then()
            .statusCode(200)
            .body("flags", Matchers.hasItem(UserFlags.Onboarded.value()));
    }
}
