package dev.roelofr.service;

import dev.roelofr.domain.Team;
import dev.roelofr.repository.DistrictRepository;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static dev.roelofr.Constants.LocaleDutch;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class DistrictService {
    private final DistrictRepository districtRepository;

    public Optional<Team> findByNameOptional(String name) {
        return districtRepository.findByName(name.trim().toLowerCase(LocaleDutch));
    }

    public @Nullable Team findByName(String name) {
        return findByNameOptional(name).orElse(null);
    }
}
