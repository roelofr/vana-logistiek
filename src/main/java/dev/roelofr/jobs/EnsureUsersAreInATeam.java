package dev.roelofr.jobs;

import dev.roelofr.domain.Team;
import dev.roelofr.repository.TeamRepository;
import dev.roelofr.repository.UserRepository;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class EnsureUsersAreInATeam {
    private static final String DEFAULT_TEAM_NAME = "Default Team";
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Startup
    @Transactional
    @Priority(Priorities.Repair)
    public void runStartup() {
        var defaultTeam = findOrCreateDefaultTeam();
        assignOrphanUsersToDefaultTeam(defaultTeam);
    }

    @Transactional
    @Scheduled(every = "5M")
    public void runScheduled() {
        var defaultTeam = findOrCreateDefaultTeam();
        assignOrphanUsersToDefaultTeam(defaultTeam);
    }

    private Team findOrCreateDefaultTeam() {
        try {
            return teamRepository.findSystemTeam(DEFAULT_TEAM_NAME);
        } catch (NoResultException e) {
            var newTeam = Team.builder()
                .name(DEFAULT_TEAM_NAME)
                .system(true)
                .build();

            teamRepository.persist(newTeam);

            log.info("Created default team");

            return newTeam;
        }
    }

    private void assignOrphanUsersToDefaultTeam(@Nonnull Team defaultTeam) {
        userRepository.find("team IS NULL")
            .stream()
            .forEach(user -> {
                log.info("Assigned user {} to default team", user);
                user.setTeam(defaultTeam);
            });
    }

}
