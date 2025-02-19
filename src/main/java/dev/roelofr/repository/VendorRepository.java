package dev.roelofr.repository;

import dev.roelofr.domain.Vendor;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class VendorRepository implements PanacheRepository<Vendor> {
    public List<Vendor> listAllSorted() {
        return list("#Vendor.getAllSorted");
    }
}
