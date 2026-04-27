package dev.roelofr.it.issue;

import dev.roelofr.config.Roles;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
@TestSecurity(user = "test", roles = {Roles.User})
public class IssueCreationIT {
    /**
     * Test dat in het Issue-domein een gebruiker een issue kan aanmaken, en dat hier een goed resultaat uit komt.
     * Een issue heeft een vendorId nodig, en een subject.
     */
    @Test
    public void createAndFetchNewIssue() {
        var chatUrl = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "subject": "Something went wrong",
                    "vendorId": 1
                }
                """)
            .when()
            .post("/issue")
            .then()
            .statusCode(HttpStatus.SC_CREATED)
            .and()
            .extract()
            .body().asString();

        RestAssured.given()
            .header("Accept", "application/json")
            .when()
            .get(chatUrl)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .and()
            .body("subject", Matchers.equalTo("Something went wrong"))
            .body("vendor.id", Matchers.comparesEqualTo(1));
    }
}
