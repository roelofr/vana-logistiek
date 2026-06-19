package dev.roelofr.features.chat;

import dev.roelofr.TestUtil;
import dev.roelofr.config.Roles;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
@QuarkusTest
@TestSecurity(user = "test", roles = {Roles.User})
public class ChatEntryCreationFeatureTest {
    @Inject
    TestUtil testUtil;

    /**
     * Test dat in het Chat-domein een gebruiker een chat kan aanmaken met een andere gebruiker.
     * Zou een chat moeten opleveren met twee gebruikers: de tegenpartij en de gebruiker zelf (impliciet).
     */
    @Test
    public void createMessageEntry() {
        var user = testUtil.createUser("test", null);
        var chat = testUtil.createChat(List.of("test"), null);

        RestAssured.given()
            .accept(ContentType.JSON)
            .when()
            .get("/chats/by-id/{key}", chat.getId())
            .then()
            .statusCode(200);

        RestAssured.given()
            .accept(ContentType.JSON)
            .when()
            .get("/chats/by-id/{key}/entries", chat.getId())
            .then()
            .statusCode(200)
            .body(Matchers.empty());

        final var testMessage = "Hello Beautiful World!";

        RestAssured.given()
            .contentType(ContentType.JSON)
            .multiPart("message", testMessage)
            .when()
            .post("/chats/by-id/{id}/entries", chat.getId())
            .then()
            .statusCode(200)
            .body(Matchers.iterableWithSize(1))
            .body(".[0].type", Matchers.equalTo("message"))
            .body(".[0].message", Matchers.equalTo(testMessage));

        RestAssured.given()
            .accept(ContentType.JSON)
            .when()
            .get("/chats/by-id/{key}/entries", chat.getId())
            .then()
            .statusCode(200)
            .body(Matchers.iterableWithSize(1));
    }

    /**
     * Test dat in het Chat-domein een gebruiker een chat kan aanmaken met een groep. Deze groep zit de gebruiker zelf
     * ook in, en dit resulteert in een chat met één partij: de groep
     */
    @Test
    public void createAndFetchSingleGroupChat() {
        var user = testUtil.createUser("test", null);
        var userGroup = user.getGroups().getFirst();

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
