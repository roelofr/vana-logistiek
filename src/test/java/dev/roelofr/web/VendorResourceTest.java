package dev.roelofr.web;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestHTTPEndpoint(VendorResource.class)
public class VendorResourceTest {
    @Test
    public void testHelloEndpoint() {
        when().get()

            .then()
            .statusCode(200)
            .log().ifValidationFails()

            .body(".[0].name", is("Test One"))
            .body(".[0].number", is("100a"))

            .body(".[1].name", is("Test Two"))
            .body(".[1].number", is("1100b"));
    }
}
