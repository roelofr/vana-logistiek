package dev.roelofr.rest.resources;

import dev.roelofr.config.Roles;
import dev.roelofr.domain.User;
import dev.roelofr.repository.UserRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static dev.roelofr.DomainHelper.EMAIL_ADMIN;
import static dev.roelofr.DomainHelper.EMAIL_NEW;
import static dev.roelofr.DomainHelper.EMAIL_USER;
import static io.restassured.RestAssured.when;
import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@QuarkusTest
@TestHTTPEndpoint(UserResource.class)
class UserResourceTest {
    @InjectMock
    UserRepository userRepository;

    @Test
    @TestSecurity(user = EMAIL_NEW)
    void testUnderprivileged() {
        given(userRepository.findById(1L))
            .willReturn(new User());

        when()
            .get("/")
            .then()
            .onFailMessage("UserResource::getList")
            .statusCode(403);

        when()
            .get("/1")
            .then()
            .onFailMessage("UserResource::getUser")
            .statusCode(403);

        when()
            .post("/1")
            .then()
            .onFailMessage("UserResource::postUser")
            .statusCode(403);
    }

    @Test
    @TestSecurity(user = EMAIL_USER, roles = {Roles.User})
    void testNonAdmin() {
        given(userRepository.findById(1L))
            .willReturn(new User());

        when()
            .post("/1")
            .then()
            .onFailMessage("UserResource::postUser")
            .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    @Disabled("Something with mocking doesn't work yet.")
    @TestSecurity(user = EMAIL_ADMIN, roles = {Roles.Admin})
    void getList() {
        var resultUser1 = User.builder().name("Test").active(true).build();
        var resultUser2 = User.builder().name("Two").active(false).build();

        var queryMock = Mockito.mock(PanacheQuery.class);
        given(queryMock.project(any())).willReturn(queryMock);
        given(queryMock.list()).willReturn(List.of(resultUser1, resultUser2));

        given(userRepository.findAll())
            .willReturn(queryMock);

        when()
            .get("/")
            .then()
            .statusCode(200)
            .body(".[0].name", equalTo("Test"))
            .body(".[1].name", equalTo("Two"));
    }

    @Test
    void getUser() {
        // TODO
    }
}
