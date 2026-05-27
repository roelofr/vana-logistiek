package dev.roelofr.domains.chat;

import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatRepository;
import dev.roelofr.domains.users.UserTestService;
import dev.roelofr.domains.users.model.UserRepository;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestHTTPEndpoint(ChatResource.class)
class ChatResourceTest {
    @Inject
    UserRepository userRepository;
    @Inject
    ChatRepository chatRepository;
    @Inject
    ChatResource chatResource;
    @Inject
    UserTestService userTestService;

    @BeforeEach
    void deleteAllChats() {
        QuarkusTransaction.requiringNew().run(chatRepository::deleteAll);
    }

    @Test
    @TestSecurity(user = "")
    void indexAsGuest() {
        RestAssured.given()
            .when()
            .get("/")
            .then()
            .statusCode(401);
    }

    @Test
    @TestSecurity(user = "test@example.com")
    void indexWithoutChats() {
        RestAssured.given()
            .when()
            .get("/")
            .then()
            .statusCode(200)
            .body("size()", is(0));
    }

    @Test
    @TestSecurity(user = "test@example.com")
    void indexWithResults() {
        var chat = QuarkusTransaction.requiringNew().call(() -> {
            var newChat = new Chat("Test");
            chatRepository.persist(newChat);

            var user = userTestService.findTestUser("test");

            newChat.addUser(user);

            return newChat;
        });

        Assumptions.assumeTrue(chat != null);

        RestAssured.given()
            .when()
            .get("/")
            .then()
            .statusCode(200)
            .body("size()", is(1))
            .body("[0].title", is("Test"));
    }


    @Test
    @TestSecurity(user = "")
    void createAsGuest() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .post("/")
            .then()
            .statusCode(401);
    }

    @Test
    @TestSecurity(user = "test@example.com")
    void create() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "title": "Hello World",
                    "members": [
                        {
                            "type": "group",
                            "id": 1
                        }
                    ]
                }
                """)
            .when()
            .get("/")
            .then()
            .statusCode(201)
            .body("title", is("Hello World"));
    }

    @Test
    void getById() {
    }

    @Test
    void testGetById() {
    }

    @Test
    void getEntries() {
    }
}
