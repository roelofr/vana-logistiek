package dev.roelofr.service;

import dev.roelofr.domain.Team;
import dev.roelofr.repository.TeamRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    public List<Team> findActiveTeams() {
        return List.of();
    }

    public Optional<Team> findTeamLike(String word) {
        return teamRepository.find("name = :name or name like :name-end or name like :name-start",
            Parameters.with("name", word)
                .and("name-end", "%" + word)
                .and("name-start", word + "%")
        ).firstResultOptional();
    }

    @Transactional
    public Team createTeam(String name) {
        var team = Team.builder()
            .name(name)
            .build();

        teamRepository.persist(team);

        return team;
    }
}
