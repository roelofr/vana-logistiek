package dev.roelofr.repository;

import dev.roelofr.domain.District;
import dev.roelofr.domain.Vendor;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class VendorRepository implements PanacheRepository<Vendor> {
    public List<Vendor> listAllSorted() {
        return list("#Vendor.getAllSorted");
    }

    public List<Vendor> listAllSortedWithPreferentialDistrict(District district) {
        var allInDistrict = list("#Vendor.getSortedInDistrict", district);
        var notInDistrict = list("#Vendor.getSortedNotInDistrict", district);

        return Stream.concat(allInDistrict.stream(), notInDistrict.stream()).collect(Collectors.toList());
    }
}
