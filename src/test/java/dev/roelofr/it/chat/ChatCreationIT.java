package dev.roelofr.it.chat;

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
public class ChatCreationIT {
    /**
     * Test dat in het Chat-domein een gebruiker een chat kan aanmaken met een andere gebruiker.
     * Zou een chat moeten opleveren met twee gebruikers: de tegenpartij en de gebruiker zelf (impliciet).
     */
    @Test
    public void createAndFetchUserToUserChat() {
        var chatId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "My Test Chat",
                    "parties": [
                        {"type": "user", "id": 2}
                    ]
                }
                """)
            .when()
            .post("/chat")
            .then()
            .statusCode(HttpStatus.SC_CREATED)
            .and()
            .extract()
            .jsonPath().getInt("id");

        RestAssured.given()
            .accept(ContentType.JSON)
            .when()
            .get("/chat/{id}", chatId)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .and()
            .body("id", Matchers.comparesEqualTo(chatId))
            .body("type", Matchers.equalTo("user"))
            .body("name", Matchers.equalTo("My Test Chat"))
            .body("parties", Matchers.iterableWithSize(2));
    }

    /**
     * Test dat in het Chat-domein een gebruiker een chat kan aanmaken met een groep. Deze groep zit de gebruiker zelf
     * ook in, en dit resulteert in een chat met één partij: de groep
     */
    @Test
    public void createAndFetchSingleGroupChat() {
        var chatId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "My Test Group Chat",
                    "parties": [
                        {"type": "group", "id": 1}
                    ]
                }
                """)
            .when()
            .post("/chat")
            .then()
            .statusCode(HttpStatus.SC_CREATED)
            .and()
            .extract()
            .jsonPath().getInt("id");

        RestAssured.given()
            .accept(ContentType.JSON)
            .when()
            .get("/chat/{id}", chatId)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .and()
            .body("id", Matchers.comparesEqualTo(chatId))
            .body("type", Matchers.equalTo("group"))
            .body("name", Matchers.equalTo("My Test Group Chat"))
            .body("parties", Matchers.iterableWithSize(1));
    }
}
