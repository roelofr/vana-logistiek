package dev.roelofr.features.issue;

import dev.roelofr.TestUtil;
import dev.roelofr.config.Roles;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestSecurity(user = "test", roles = {Roles.User})
public class IssueCreationFeatureTest {
    @Inject
    TestUtil testUtil;

    /**
     * Test dat in het Issue-domein een gebruiker een issue kan aanmaken, en dat hier een goed resultaat uit komt.
     * Een issue heeft een vendorId nodig, en een subject.
     */
    @Test
    public void createAndFetchNewIssue() {
        var vendor = testUtil.createVendor("403a", "Steve Stevenson");

        var chatUrl = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "subject": "Something went wrong",
                    "vendorId": %d
                }
                """.formatted(vendor.getId()))
            .when()
            .post("/issue")
            .then()
            .statusCode(HttpStatus.SC_CREATED)
            .and()
            .extract()
            .body().asString();

        var ticketId = RestAssured.given()
            .header("Accept", "application/json")
            .when()
            .get(chatUrl)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .and()
            .body("subject", Matchers.equalTo("Something went wrong"))
            .body("vendor.id", Matchers.comparesEqualTo(vendor.getId()))
            .and()
            .log().body()
            .and()
            .extract().jsonPath().getLong("id");

        // Ensure chat is created and available
        RestAssured.given()
            .header("Accept", "application/json")
            .when()
            .get("/chats/by-label/{id}", String.format("TICKET_%d", ticketId))
            .then()
            .statusCode(HttpStatus.SC_OK);
    }
}
