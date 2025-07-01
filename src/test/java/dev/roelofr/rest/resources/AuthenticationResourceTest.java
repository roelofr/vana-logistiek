package dev.roelofr.rest.resources;

import dev.roelofr.MockDomainHelper;
import dev.roelofr.config.Roles;
import dev.roelofr.domain.enums.TicketStatus;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.TicketRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static io.restassured.RestAssured.when;
import static io.restassured.RestAssured.with;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.mockito.BDDMockito.given;
import static dev.roelofr.MockDomainHelper.TEST_USER;

@QuarkusTest
@TestHTTPEndpoint(TicketResource.class)
class AuthenticationResourceTest {

    @Test
    void getMe() {
        // TODO
    }

    @Test
    void postLoginHappyTrail() {
        with()
            .contentType(ContentType.JSON)
            .body("""
                {
                  "username": "%s",
                  "password": "testtest"
                }
                """.formatted(TEST_USER))
            .post("/login")
            .then()
            .statusCode(200);
    }
}
