package dev.roelofr.jobs;

import dev.roelofr.domain.Team;
import dev.roelofr.repository.TeamRepository;
import io.quarkus.runtime.Startup;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ProvisionTeamsFromFile {
    private final TeamRepository teamRepository;

    @Startup
    @Transactional
    @Priority(Priorities.Provision)
    void createTeamsFromResourceFile() {
        var teams = loadResource();

        log.info("Loaded {} teams from file", teams.size());

        for (var sourceTeam : teams) {
            var teamOptional = teamRepository.findByName(sourceTeam.getName());
            if (teamOptional.isPresent()) {
                var team = teamOptional.get();
                if (!team.isSystem())
                    log.warn("Found existing team [{}] to meet required team [{}], but it is not a system team!");

                log.info("Required team [{}] exists as [{}]", sourceTeam.getName(), team.getName());
                continue;
            }

            var team = Team.builder()
                .name(sourceTeam.getName())
                .icon(sourceTeam.getIcon())
                .colour(sourceTeam.getColour())
                .system(true)
                .build();

            log.info("Created system team [{}]", team.getName());

            teamRepository.persist(team);
        }
    }

    private List<ProvisionTeam> loadResource() {
        var yaml = new Yaml(new Constructor(ProvisionFile.class, new LoaderOptions()));
        var inputStream = getClass().getClassLoader().getResourceAsStream("/required-teams.yaml");

        ProvisionFile file = yaml.load(inputStream);
        return file.teams;
    }

    @Data
    public static class ProvisionFile {
        List<ProvisionTeam> teams;
    }

    @Data
    public static class ProvisionTeam {
        String name;
        String icon;
        String colour;
    }
}
