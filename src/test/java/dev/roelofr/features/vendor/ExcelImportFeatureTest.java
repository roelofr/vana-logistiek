package dev.roelofr.features.vendor;

import dev.roelofr.Roles;
import dev.roelofr.domains.vendor.VendorTestData;
import dev.roelofr.domains.vendor.model.VendorRepository;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ExcelImportFeatureTest {
    @Inject
    VendorRepository vendorRepository;

    @Test
    @TestSecurity(user = "admin", roles = {Roles.Admin})
    void importVendorList() {
        var uploadFile = new File("src/test/resources/test-xlsx/valid-file.xlsx");
        Assumptions.assumeTrue(uploadFile.exists(), String.format("Required input file %s does not exist", uploadFile.getName()));

        RestAssured.given()
            .multiPart("file", uploadFile.getAbsoluteFile())
            .when()
            .post("/import")
            .then()
            .statusCode(200);

        QuarkusTransaction.requiringNew().run(() ->
            VendorTestData.validXlsxFileVendors.forEach(vendor -> {
                var result = vendorRepository.findByNumber(vendor.number());
                assertTrue(result.isPresent(), () -> String.format("Expected a vendor with number %s to exist", vendor.number()));

                var resultVendor = result.get();
                assertEquals(vendor.name(), resultVendor.getName(), () -> String.format(
                    "Expected vendor %s to have name %s, but got %s instead",
                    vendor.number(),
                    vendor.name(),
                    resultVendor.getName()
                ));

                assertTrue(
                    resultVendor.getDistrict().getName().toLowerCase().contains(vendor.district().toLowerCase()), () -> String.format(
                        "Expected vendor %s to be in a district with name %s, but got %s instead",
                        vendor.number(),
                        vendor.district(),
                        resultVendor.getDistrict().getName()
                    ));
            })
        );
    }

}
