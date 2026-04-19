package dev.roelofr.domains.vendor;

import io.quarkus.panache.common.Sort;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import static dev.roelofr.Constants.LocaleDutch;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class DistrictService {
    private final DistrictRepository districtRepository;
    
    public List<District> getAllSorted() {
        return districtRepository.listAll(
            Sort.by("name", Sort.Direction.Ascending)
                .and("id", Sort.Direction.Ascending)
        );
    }

    public Optional<District> findByNameOptional(String name) {
        return districtRepository.findByName(name.trim().toLowerCase(LocaleDutch));
    }

    public @Nullable District findByName(String name) {
        return findByNameOptional(name).orElse(null);
    }

    public List<District> findWithoutTeam() {
        return districtRepository.find("team = null").list();
    }
}
