package dev.roelofr.service;

import dev.roelofr.domain.Team;
import dev.roelofr.repository.TeamRepository;
import jakarta.enterprise.context.ApplicationScoped;
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
        return teamRepository.find("#Team.getLikeName", word).firstResultOptional();
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
