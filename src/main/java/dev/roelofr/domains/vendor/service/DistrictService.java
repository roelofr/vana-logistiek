package dev.roelofr.domains.vendor.service;

import dev.roelofr.domains.vendor.model.District;
import dev.roelofr.domains.vendor.model.DistrictRepository;
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
        return districtRepository.listAllSorted();
    }

    public Optional<District> findByNameOptional(String name) {
        return districtRepository.findByName(name.trim().toLowerCase(LocaleDutch));
    }

    public @Nullable District findByName(String name) {
        return findByNameOptional(name).orElse(null);
    }

    public List<District> findWithoutGroup() {
        return districtRepository.find("group IS NULL").list();
    }

    public Optional<District> findById(long id) {
        return districtRepository.findByIdOptional(id);
    }
}
