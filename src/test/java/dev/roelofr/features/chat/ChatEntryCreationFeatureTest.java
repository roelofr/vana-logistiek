package dev.roelofr.features.chat;

import dev.roelofr.TestUtil;
import dev.roelofr.config.Roles;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
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
            .body(".", Matchers.iterableWithSize(0));

        final var testMessage = "Hello Beautiful World!";

        RestAssured.given()
            .multiPart("message", testMessage)
            .when()
            .post("/chats/by-id/{id}/entries", chat.getId())
            .then()
            .statusCode(200)
            .body(".", Matchers.iterableWithSize(1))
            .body(".[0].type", Matchers.equalTo("message"))
            .body(".[0].message", Matchers.equalTo(testMessage));

        RestAssured.given()
            .accept(ContentType.JSON)
            .when()
            .get("/chats/by-id/{key}/entries", chat.getId())
            .then()
            .statusCode(200)
            .body(".", Matchers.iterableWithSize(1));
    }
}
