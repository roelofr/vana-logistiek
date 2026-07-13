package dev.roelofr.features.chat;

import dev.roelofr.TestUtil;
import dev.roelofr.config.Roles;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
@TestSecurity(user = "test", roles = {Roles.User})
public class ChatCreationFeatureTest {
    @Inject
    TestUtil testUtil;

    /**
     * Test dat in het Chat-domein een gebruiker een chat kan aanmaken met een andere gebruiker.
     * Zou een chat moeten opleveren met twee gebruikers: de tegenpartij en de gebruiker zelf (impliciet).
     */
    @Test
    public void createAndFetchUserToUserChat() {
        var user2 = testUtil.createUser("test2", null);

        var chatId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "My Test Chat",
                    "parties": [
                        {"type": "user", "id": %d}
                    ]
                }
                """.formatted(user2.getId()))
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
        var user = testUtil.createUser("test", null);
        var userGroup = user.getGroup();

        Assumptions.assumeTrue(userGroup != null);

        var user2 = testUtil.createUser("test2", List.of(userGroup.getName()));

        var chatId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "My Test Group Chat",
                    "parties": [
                        {"type": "group", "id": %d}
                    ]
                }
                """.formatted(userGroup.getId()))
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
