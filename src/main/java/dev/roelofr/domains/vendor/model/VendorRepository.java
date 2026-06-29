package dev.roelofr.domains.vendor.model;

import dev.roelofr.domains.users.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class VendorRepository implements PanacheRepository<Vendor> {
    public List<Vendor> listAllSorted() {
        return list("#Vendor.getAllSorted");
    }

    public List<Vendor> listAllSortedForUser(User user) {
        return list("#Vendor.getAllSortedForUser", Map.of("user", user));
    }

    public List<Vendor> listInDistrict(District district) {
        return list("#Vendor.getSortedInDistrict", Map.of("district", district));
    }

    public List<Vendor> findByNumbers(Collection<String> numbers) {
        return list("LOWER(number) IN ?1", numbers.stream().map(String::toLowerCase).toList());
    }

    public Optional<Vendor> findByNumber(String number) {
        if (number == null || number.isBlank())
            return Optional.empty();

        return find("LOWER(number) = LOWER(?1)", number).singleResultOptional();
    }
}
