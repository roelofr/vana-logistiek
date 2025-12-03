package dev.roelofr.service;

import dev.roelofr.DomainHelper;
import dev.roelofr.domain.Vendor;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

@QuarkusTest
class VendorServiceTest {
    @Inject
    UserRepository userRepository;
    @Inject
    VendorService vendorService;
    @Inject
    DistrictRepository districtRepository;

    @Test
    void listVendors() {
    }

    @Test
    void listVendorsByUserWithoutDistrict() {
        var userOptional = userRepository.findByEmailOptional(DomainHelper.EMAIL_FROZEN);
        assumeTrue(userOptional.isPresent());

        var user = userOptional.get();
//        assumeTrue(user.getDistrict() == null);

        var vendors = vendorService.listVendors(user);

        var testVendors = vendors.stream()
            .filter(vendor -> vendor.getName().startsWith("Test "))
            .map(Vendor::getNumber)
            .toList();

        Assertions.assertIterableEquals(List.of(
            "100a",
            "1100a",
            "1202"
        ), testVendors);
    }

    @Test
    void listVendorsByUserDistrict() {
        var userOptional = userRepository.findByEmailOptional(DomainHelper.EMAIL_CP);
        assumeTrue(userOptional.isPresent());

        var user = userOptional.get();
//        assumeTrue(user.getDistrict() != null);
//        assumeTrue(user.getDistrict().getName().equals("test-blauw"));

        var vendors = vendorService.listVendors(user);

        var testVendors = vendors.stream()
            .filter(vendor -> vendor.getName().startsWith("Test "))
            .map(Vendor::getNumber)
            .toList();

        Assertions.assertIterableEquals(List.of(
            "1100a",
            "1202",
            "100a"
        ), testVendors);
    }
}
