package dev.roelofr.rest.resources;

import dev.roelofr.integrations.hanko.HankoClient;
import dev.roelofr.integrations.hanko.model.SessionValidationResponse;
import dev.roelofr.repository.UserRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static dev.roelofr.DomainHelper.EMAIL_FROZEN;
import static dev.roelofr.DomainHelper.EMAIL_USER;
import static io.restassured.RestAssured.when;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;

@Slf4j
@QuarkusTest
@TestHTTPEndpoint(AuthenticationResource.class)
class AuthenticationResourceTest {
    @Inject
    UserRepository userRepository;

    @InjectMock
    @RestClient
    HankoClient hankoClient;

    @Test
    @TestSecurity(user = EMAIL_USER)
    void headVerify() {
        when().head()
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(200);
    }

    @Test
    @TestSecurity(user = EMAIL_USER)
    void getMe() {
        var user = userRepository.findByEmailOptional(EMAIL_USER).orElseThrow();

        when().get("/me")
            .then()
            .log().ifValidationFails()
            .statusCode(200)

            .assertThat()
            .body("user.id", equalTo(user.getId().intValue()))
            .body("user.name", equalTo(user.getName()))
            .body("user.email", equalTo(EMAIL_USER));
    }

    @Test
    void postConsume() {
        var user = userRepository.find("email", EMAIL_USER).singleResult();

        var jwt = Jwt.subject(user.getProviderId())
            .sign();

        given(hankoClient.validate(jwt))
            .willReturn(new SessionValidationResponse(true, Instant.MAX, null, null));

        with()
            .contentType(ContentType.JSON)
            .body("""
                {"token": "%s"}
                """.formatted(jwt))
            .post("/consume")
            .then()
            .statusCode(200)
            .assertThat()

            .body("name", equalTo(user.getName()))
            .body("email", equalTo(user.getEmail()))
            .body("roles", equalTo(user.getRoles()))
            .body("jwt", any(String.class));

        then(hankoClient).should(never()).getUser(anyString(), anyString());
    }

    @Test
    void postConsumeInactive() {
        var user = userRepository.find("email", EMAIL_FROZEN).singleResult();

        var jwt = Jwt.subject(user.getProviderId())
            .sign();

        given(hankoClient.validate(jwt))
            .willReturn(new SessionValidationResponse(true, Instant.MAX, null, null));

        with()
            .contentType(ContentType.JSON)
            .body("""
                {"token": "%s"}
                """.formatted(jwt))
            .post("/consume")
            .then()
            .assertThat()
            .statusCode(403);

        then(hankoClient).should(never()).getUser(anyString(), anyString());
    }
}
