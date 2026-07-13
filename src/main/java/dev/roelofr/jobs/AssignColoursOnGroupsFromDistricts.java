package dev.roelofr.jobs;

import dev.roelofr.domains.users.model.GroupRepository;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AssignColoursOnGroupsFromDistricts {
    private final GroupRepository groupRepository;

    @Startup
    @Transactional
    void assignColours() {
        var groups = groupRepository.listAll();

        for (var group : groups) {
            if (group.getDistricts().size() != 1)
                continue;

            var colour = group.getDistricts().getFirst().getColour();
            if (colour == null || colour.equalsIgnoreCase(group.getColour()))
                continue;

            log.info("Assigned colour {} to group {}", colour, group.getName());
            group.setColour(colour);
        }
    }
}
