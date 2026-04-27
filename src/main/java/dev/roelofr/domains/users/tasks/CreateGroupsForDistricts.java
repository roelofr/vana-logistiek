package dev.roelofr.domains.users.tasks;

import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.GroupRepository;
import dev.roelofr.domains.vendor.model.District;
import dev.roelofr.domains.vendor.model.DistrictRepository;
import dev.roelofr.events.ModelCreatedEvent;
import dev.roelofr.events.ModelUpdatedEvent;
import dev.roelofr.jobs.Priorities;
import io.quarkus.runtime.Startup;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CreateGroupsForDistricts {
    private final GroupRepository groupRepository;
    private final DistrictRepository districtRepository;

    @Startup
    @Transactional
    @Priority(Priorities.Repair)
    public void observeStartup() {
        for (var district : districtRepository.listAll()) {
            createGroupIfMissing(district);
        }
    }

    @Transactional
    public void observeCreation(@ObservesAsync ModelCreatedEvent<District> event) {
        var district = event.getModel();
        createGroupIfMissing(district);
    }

    @Transactional
    public void observeUpdates(@ObservesAsync ModelUpdatedEvent<District> event) {
        var district = event.getModel();
        createGroupIfMissing(district);
    }

    void createGroupIfMissing(District district) {
        if (groupRepository.findByName(district.getName()).isPresent()) {
            log.info("Not creating a new group for district {}, it already exists", district.getName());
            return;
        }

        var group = Group.builder()
            .name(district.getName())
            .build();

        groupRepository.persist(group);

        log.info("Created new group for district {} with ID {}", district.getName(), group.getId());
    }
}
