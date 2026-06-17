package dev.roelofr.domains.chat;

import dev.roelofr.Roles;
import dev.roelofr.TestUtil;
import dev.roelofr.domains.chat.dto.CreateChatRequest;
import dev.roelofr.domains.chat.model.ChatMessage;
import dev.roelofr.domains.chat.model.ChatRepository;
import dev.roelofr.domains.users.UserTestService;
import dev.roelofr.domains.users.model.UserRepository;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
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

    @Inject
    TestUtil testUtil;
    @Inject
    ChatService chatService;

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
    @TestSecurity(user = "user@example.com")
    void indexWithoutChats() {
        RestAssured.given()
            .when()
            .get("/")
            .then()
            .statusCode(200)
            .body("size()", is(0));
    }

    @Test
    @TestSecurity(user = "user@example.com")
    void indexWithResults() {
        var me = testUtil.createUser("user", List.of("alpha", "bravo"));
        var chat1 = testUtil.createChat(List.of("user@example.com"), null);
        var chat2 = testUtil.createChat(null, List.of("alpha"));

        Assumptions.assumeTrue(chat1 != null);
        Assumptions.assumeTrue(chat2 != null);

        RestAssured.given()
            .when()
            .get("/")
            .then()
            .statusCode(200)
            .body("size()", is(2))
            .body("[0].title", is(chat1.getTitle()))
            .body("[1].title", is(chat2.getTitle()));
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
    @TestSecurity(user = "user@example.com")
    @JwtSecurity(claims = {
        @Claim(key = "sub", value = "user@example.com"),
        @Claim(key = "groups", value = Roles.Wijkhouder)
    })
    void create() {
        var me = testUtil.createUser("user", List.of("alpha", "bravo"));
        var groupId = me.getGroups().getFirst().getId();

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(
                CreateChatRequest.builder()
                    .title("Hello World")
                    .members(List.of(
                        CreateChatRequest.ChatMember.builder()
                            .type(CreateChatRequest.MemberType.Group)
                            .id(groupId)
                            .build()
                    ))
                    .build()
            )
            .when()
            .post("/")
            .then()
            .statusCode(200)
            .body("id", is(notNullValue()))
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

    @Test
    @TestSecurity(user = "bob")
    void createChatEntry() {
        var user = testUtil.createUser("bob", null);

        var chat = chatService.findWithoutKeyByUser(user, 1, 10).getFirst();

        Assumptions.assumeTrue(chat != null);
        var testMessage = "Hello World";

        RestAssured.given()
            .multiPart("message", testMessage)
            .when()
            .post("/by-id/{id}/entries", chat.getId())
            .then()
            .body("size()", is(1))
            .body("[0].type", equalTo(ChatMessage.TYPE))
            .body("[1].message", equalTo(testMessage));
    }
}
