package dev.roelofr.it;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@Slf4j
@QuarkusTest
public class VendorHappyFlowTest {
    @Test
    public void vendorHappyFlow() {
        // TODO Create vendor
        int newVendorId = given()
            .contentType(ContentType.JSON)
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
            .post("/tickets")
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
