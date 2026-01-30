package dev.roelofr.repository;

import dev.roelofr.domain.Team;
import dev.roelofr.domain.Vendor;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class VendorRepository implements PanacheRepository<Vendor> {
    public List<Vendor> listAllSorted() {
        return list("#Vendor.getAllSorted");
    }

    public List<Vendor> listAllSortedWithPreferentialTeam(Team team) {
        var allInTeam = list("#Vendor.getSortedInTeam", team);
        var notInTeam = list("#Vendor.getSortedNotInTeam", team);

        return Stream.concat(allInTeam.stream(), notInTeam.stream())
            .collect(Collectors.toList());
    }

    public Optional<Vendor> findByNumber(String number) {
        if (number == null || number.isBlank())
            return Optional.empty();

        return find("number = ?1", number.toLowerCase()).singleResultOptional();
    }
}
