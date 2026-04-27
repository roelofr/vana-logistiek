package dev.roelofr.jobs;

import dev.roelofr.domains.users.GroupService;
import dev.roelofr.domains.vendor.service.DistrictService;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AddGroupsToOrphanDistricts {
    private final DistrictService districtService;
    private final GroupService groupService;

    @Startup
    @Transactional
    @Scheduled(every = "6h")
    @Priority(Priorities.Repair)
    void determineMissingTeams() {
        var orphanDistricts = districtService.findWithoutTeam();

        if (orphanDistricts.isEmpty()) {
            log.info("No orphan districts found");
            return;
        }

        log.info("Processing {} orphan districts", orphanDistricts.size());

        for (var district : orphanDistricts) {
            var namedTeam = groupService.findGroupLike(district.getName());

            if (namedTeam.isPresent()) {
                var team = namedTeam.get();

                log.info("Using existing team {} named {} for district {}", team.getId(), team.getName(), district.getName());
                district.setGroup(team);

                continue;
            }

            var newTeam = groupService.createGroup(String.format("Wijk %s", district.getName()));

            log.info("Created new team {} named {} for district {}", newTeam.getId(), newTeam.getName(), district.getName());

            district.setGroup(newTeam);
        }
    }
}
