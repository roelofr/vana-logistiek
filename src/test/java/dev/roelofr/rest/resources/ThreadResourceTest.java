package dev.roelofr.rest.resources;

import dev.roelofr.domain.projections.ListThread;
import dev.roelofr.repository.ThreadRepository;
import dev.roelofr.service.ThreadService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.util.List;

@QuarkusTest
@TestHTTPEndpoint(ThreadResource.class)
class ThreadResourceTest {
    @InjectMock
    ThreadRepository threadRepository;
    @InjectMock
    ThreadService threadService;

    @Test
    @TestSecurity(user = "")
    void testUnauthenticated() {
        // Get /
        RestAssured
            .with()
            .get("/")
            .then()
            .statusCode(HttpStatus.SC_UNAUTHORIZED);

        // GET /{id}
        RestAssured
            .get("/44")
            .then()
            .statusCode(HttpStatus.SC_UNAUTHORIZED);

        // POST /
        RestAssured
            .with()
            .contentType(ContentType.JSON)
            .body("{}")
            .post("/")
            .then()
            .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @TestSecurity(user = "test", roles = {})
    void listThreads() {
        var dummyListThread = ListThread.builder()
            .subject("Test One")
            .build();

        BDDMockito.given(threadService.findAll(false))
            .willReturn(
                List.of(dummyListThread)
            );

        RestAssured.get()
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.SC_OK)
            .body("[0].subject", Matchers.equalTo(dummyListThread.subject()));

        BDDMockito.then(threadService)
            .should(Mockito.times(1))
            .findAll(false);

        BDDMockito.then(threadService)
            .shouldHaveNoMoreInteractions();
    }

    @Test
    void createThread() {
    }

    @Test
    void showThread() {
    }
}
