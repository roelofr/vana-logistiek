package dev.roelofr.jobs;

import dev.roelofr.repository.VendorRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ResetVendorsOnBoot {
    private final VendorRepository vendorRepository;

    @Transactional
    void ResetVendorNumbers(@Observes StartupEvent startupEvent) {
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
