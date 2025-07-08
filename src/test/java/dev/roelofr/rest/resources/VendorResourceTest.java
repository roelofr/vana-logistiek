package dev.roelofr.rest.resources;

import dev.roelofr.domain.TestTicket;
import dev.roelofr.domain.TestVendor;
import dev.roelofr.service.TicketService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.when;
import static io.restassured.RestAssured.with;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest
@TestHTTPEndpoint(VendorResource.class)
public class VendorResourceTest {
    @InjectMock
    dev.roelofr.service.VendorService vendorService;

    @InjectMock
    TicketService ticketService;

    @Test
    public void testAnonymous() {
        when().get("/")
            .then()
            .onFailMessage("getVendorList")
            .statusCode(401);

        with()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .post("/")
            .then()
            .onFailMessage("postCreateVendor")
            .statusCode(401);

        when().get("/1")
            .then()
            .onFailMessage("getVendor")
            .statusCode(401);

        when().get("/1/tickets")
            .then()
            .onFailMessage("getVendorTickets")
            .statusCode(401);
    }

    @Test
    @TestTransaction
    @TestSecurity(user = "test")
    public void getVendorList() {
        var vendor1 = TestVendor.make("Test One", "100a", "Rood");
        var vendor2 = TestVendor.make("Test Two", "1100b", "Zandbruin");

        given(vendorService.listVendors())
            .willReturn(List.of(
                vendor1,
                vendor2
            ));

        when().get()

            .then()
            .log().ifValidationFails()

            .assertThat()
            .statusCode(200)

            .body(".", iterableWithSize(2))

            .body("[0].name", is("Test One"))
            .body("[0].number", is("100a"))

            .body("[1].name", is("Test Two"))
            .body("[1].number", is("1100b"));
    }

    @Test
    @TestSecurity(user = "test")
    public void postCreateVendor() {
        final var vendor = TestVendor.make("Fairy Test", "1100a", "Zandbruin");

        given(vendorService.createVendor(
            vendor.getDistrict().getName(),
            vendor.getNumber(),
            vendor.getName()
        )).willReturn(vendor);

        with()
            .contentType(ContentType.JSON)
            .body("""
                {
                  "name": "Fairy Test",
                  "number": "1100a",
                  "district":  "Zandbruin"
                }
                """)
            .post()

            .then()
            .log().ifValidationFails()

            .assertThat()
            .statusCode(201)
            .body("name", is("Fairy Test"))
            .body("number", is("1100a"))
            .body("district.name", is("Zandbruin"));

        verify(vendorService, times(1))
            .createVendor(
                vendor.getDistrict().getName(),
                vendor.getNumber(),
                vendor.getName()
            );
    }

    @Test
    @TestSecurity(user = "test")
    public void getVendor() {
        var vendor = TestVendor.make("Test Berries", "1100b", "Zandbruin");

        given(vendorService.getVendor(1L))
            .willReturn(vendor);

        when()
            .get("/1")

            .then()
            .log().ifValidationFails()

            .assertThat()
            .statusCode(200)
            .body("name", is("Test Berries"))
            .body("number", is("1100b"));
    }

    @Test
    @TestSecurity(user = "test")
    public void getVendorNotFound() {
        given(vendorService.getVendor(1L))
            .willReturn(null);

        when()
            .get("/1")

            .then()
            .log().ifValidationFails()

            .assertThat()
            .statusCode(404);
    }

    @Test
    @TestSecurity(user = "test")
    public void getVendorTickets() {
        var vendor = TestVendor.make(1L, "Johnny Leather", "300b", "oranje");

        given(vendorService.getVendor(1L))
            .willReturn(vendor);

        given(ticketService.listByVendor(vendor))
            .willReturn(List.of(
                TestTicket.make("Test Ticket One", vendor),
                TestTicket.make("Test Ticket Two", vendor).completed()
            ));

        when()
            .get("/1/tickets")
            .then()
            .log().ifValidationFails()

            .assertThat()
            .statusCode(200)

            .body(".", iterableWithSize(2))
            .body("[0].description", is("Test Ticket One"))
            .body("[0].vendor.name", is("Johnny Leather"))
            .body("[1].description", is("Test Ticket Two"))
            .body("[1].vendor.name", is("Johnny Leather"));
    }
}
