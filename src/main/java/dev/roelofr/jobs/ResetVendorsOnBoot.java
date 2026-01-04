package dev.roelofr.jobs;

import dev.roelofr.repository.VendorRepository;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.Startup;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ResetVendorsOnBoot {
    private final VendorRepository vendorRepository;
    private final LaunchMode launchMode;

    @Startup
    @Transactional
    @Priority(Priorities.Repair)
    void resetOnStartup() {
        if (launchMode.equals(LaunchMode.TEST))
            return;

        ResetVendorNumbers();
    }

    @Transactional
    void ResetVendorNumbers() {
        var vendorsWithoutNumeric = vendorRepository.find("numberNumeric is null").list();

        if (vendorsWithoutNumeric.isEmpty()) {
            log.info("All vendors have a number :)");
        }

        for (var vendor : vendorsWithoutNumeric) {
            log.info("Recalculate {} {}", vendor.getNumber(), vendor.getName());
            vendor.determineNumberNumeric();
        }
    }
}
