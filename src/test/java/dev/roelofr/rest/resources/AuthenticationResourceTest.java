package dev.roelofr.rest.resources;

import static dev.roelofr.DomainHelper.EMAIL_USER;
import static io.restassured.RestAssured.with;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(TicketResource.class)
class AuthenticationResourceTest {

    @Test
    void getMe() {
        // TODO
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
            .statusCode(200);
    }
}
