package dev.roelofr.domains.vendor.tasks;

import dev.roelofr.domains.vendor.model.VendorRepository;
import io.quarkus.runtime.Startup;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DetermineVendorNumbers implements Runnable {
    final VendorRepository vendorRepository;

    @Startup
    @Override
    @Transactional
    public void run() {
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
