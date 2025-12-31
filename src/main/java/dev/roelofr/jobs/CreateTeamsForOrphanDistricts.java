package dev.roelofr.jobs;

import dev.roelofr.service.DistrictService;
import dev.roelofr.service.TeamService;
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
public class CreateTeamsForOrphanDistricts {
    final DistrictService districtService;

    final TeamService teamService;

    @Startup
    @Transactional
    @Priority(1000)
    @Scheduled(every = "6h")
    void determineMissingTeams() {
        var orphanDistricts = districtService.findWithoutTeam();

        if (orphanDistricts.isEmpty()) {
            log.info("No orphan districts found");
            return;
        }

        log.info("Processing {} orphan districts", orphanDistricts.size());

        for (var district : orphanDistricts) {
            var namedTeam = teamService.findTeamLike(district.getName());

            if (namedTeam.isPresent()) {
                var team = namedTeam.get();

                log.info("Using existing team {} named {} for district {}", team.getId(), team.getName(), district.getName());
                district.setTeam(team);

                continue;
            }

            var newTeam = teamService.createTeam(String.format("Wijk %s", district.getName()));

            log.info("Created new team {} named {} for district {}", newTeam.getId(), newTeam.getName(), district.getName());

            district.setTeam(newTeam);
        }
    }
}
