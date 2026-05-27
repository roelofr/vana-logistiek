package dev.roelofr.domains.vendor.service;

import dev.roelofr.domains.vendor.VendorTestData;
import dev.roelofr.domains.vendor.model.VendorRepository;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@QuarkusTest
@TestTransaction
class VendorAdminServiceTest {
    @Inject
    VendorRepository vendorRepository;
    @Inject
    VendorAdminService vendorAdminService;

    @BeforeEach
    public void prepareDataset() {
        vendorRepository.deleteAll();
    }

    @Test
    void importValidVendorList() {
        var inputFile = new File("src/test/resources/test-xlsx/valid-file.xlsx");
        Assumptions.assumeTrue(inputFile.exists(), String.format("Required input file %s does not exist", inputFile.getName()));

        var result = assertDoesNotThrow(() -> vendorAdminService.importVendorList(inputFile.getAbsoluteFile()));

        VendorTestData.validXlsxFileVendors.forEach(vendor -> {
            var resultVendorOpt = result.stream().filter(v -> v.getNumber().equals(vendor.number())).findFirst();

            assertTrue(resultVendorOpt.isPresent(), () -> String.format("Expected a vendor with number %s to exist", vendor.number()));

            var resultVendor = resultVendorOpt.get();

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
        });
    }

    @Test
    void importImageAsVendorList() {
        var inputFile = new File("src/test/resources/test-images/amanda-swanepoel.jpeg");
        Assumptions.assumeTrue(inputFile.exists(), String.format("Required input file %s does not exist", inputFile.getName()));

        var result = assertThrows(BadRequestException.class, () -> vendorAdminService.importVendorList(inputFile));

        assertNotNull(result.getMessage(), "Exception thrown but without a message");
        assertEquals("This does not seem to be an Excel file.", result.getMessage());
    }
}
