package dev.roelofr.rest.resources;

import dev.roelofr.DomainHelper;
import dev.roelofr.config.Roles;
import dev.roelofr.domain.enums.TicketStatus;
import dev.roelofr.service.TicketService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.mockito.BDDMockito.given;

@QuarkusTest
@TestHTTPEndpoint(TicketResource.class)
class TicketResourceTest {
    @InjectMock
    TicketService ticketService;

    DomainHelper domainHelper = new DomainHelper();

    @Test
    @TestSecurity()
    void testUnauthenticated() {
        when().get("/")
            .then()
            .onFailMessage("list")
            .statusCode(401);

        when().get("/by-district/test")
            .then()
            .onFailMessage("listForDistrict")
            .statusCode(401);

        when().get("/by-user/me")
            .then()
            .onFailMessage("listForUser")
            .statusCode(401);

        when().get("/1")
            .then()
            .onFailMessage("find")
            .statusCode(401);

        when().post("/")
            .then()
            .onFailMessage("create")
            .statusCode(401);
    }

    @Test
    @TestSecurity(user = "test", roles = {Roles.User})
    void list() {
        final var ticketOne = domainHelper.dummyTicket("Test List One");
        ticketOne.setStatus(TicketStatus.Resolved);
        ticketOne.setCompletedAt(LocalDateTime.now());

        final var ticketTwo = domainHelper.dummyTicket("Test List Two");
        ticketTwo.setStatus(TicketStatus.Assigned);
        ticketTwo.setAssignee(domainHelper.randomUser());

        final var ticketThree = domainHelper.dummyTicket("Test List Three");

        given(ticketService.list())
            .willReturn(List.of(
                ticketOne,
                ticketTwo
            ));

        when().get("/")
            .then()
            .log().ifValidationFails()

            .assertThat()
            .statusCode(200)

            .body(".", iterableWithSize(2))

            .body("[0].description", equalTo(ticketOne.getDescription()))
            .body("[1].description", equalTo(ticketTwo.getDescription()));
    }

    @Test
    @Disabled("listForDistrict is List.of()")
    @TestSecurity(user = "test", roles = {Roles.User})
    void listForDistrict() {
        final var district = domainHelper.randomDistrict();
        final var ticket = domainHelper.dummyTicket("Test List Three");

//        given(ticketService.findByDistrict(district.getName()))
//            .willReturn(List.of(ticket));

        when().get("/by-district/" + district.getName())
            .then()
            .log().ifValidationFails()

            .assertThat()
            .statusCode(200)
            .body("", iterableWithSize(1))
            .body("[0].description", equalTo(ticket.getDescription()));
    }

    @Test
    @Disabled("listForDistrict is List.of()")
    @TestSecurity(user = "test", roles = {Roles.User})
    void listForDistrictNotFound() {
        final String TEST_NAME = "test-not-found";

//        given(ticketService.findByDistrict(district.getName()))
//            .willThrow(List.of(ticket));

        when().get("/by-district/" + TEST_NAME)
            .then()
            .log().ifValidationFails()

            .assertThat()
            .statusCode(404);
    }

    @Test
    @Disabled("listForCurrentUser is List.of()")
    @TestSecurity(user = DomainHelper.EMAIL_USER)
    void listForCurrentUser() {
        final var ticket = domainHelper.dummyTicket("Test List Three");
        final var ticket2 = domainHelper.dummyTicket("Test List Two");

//        given(ticketService.findByDistrict(district.getName()))
//            .willReturn(List.of(ticket, ticket2));


        when().get("/by-user/me")
            .then()
            .log().ifValidationFails()

            .assertThat()
            .statusCode(200)
            .body("", iterableWithSize(2))
            .body("[0].description", equalTo(ticket.getDescription()));
    }

    @Test
    void find() {
    }

    @Test
    void create() {
    }
}
