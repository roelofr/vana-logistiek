package dev.roelofr.domains.users.tasks;

import dev.roelofr.domain.Team;
import dev.roelofr.domains.users.GroupService;
import dev.roelofr.jobs.Priorities;
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
public class CreateGroupsFromFile {
    private final TeamRepository teamRepository;
    private final GroupService groupService;

    @Startup
    @Transactional
    @Priority(Priorities.Provision)
    void createGroupsFromFile() {
        var groups = loadResource();

        log.info("Loaded {} groups from file", groups.size());

        for (var provisionGroup : groups) {
            var existingGroup = groupService.findByName(provisionGroup.getName()).orElse(null);
            if (existingGroup != null) {
                if (!existingGroup.isSystem())
                    log.warn("Found existing group [{}] to meet required group [{}], but it is not a system group!");

                log.info("Required team [{}] exists as [{}]", provisionGroup.getName(), team.getName());
                continue;
            }

            var team = Team.builder()
                .name(provisionGroup.getName())
                .icon(provisionGroup.getIcon())
                .colour(provisionGroup.getColour())
                .system(true)
                .build();

            log.info("Created system team [{}]", team.getName());

            teamRepository.persist(team);
        }
    }

    private List<ProvisionGroup> loadResource() {
        var yaml = new Yaml(new Constructor(ProvisionFile.class, new LoaderOptions()));
        var inputStream = getClass().getClassLoader().getResourceAsStream("/required-groups.yaml");

        ProvisionFile file = yaml.load(inputStream);
        return file.groups;
    }

    @Data
    public static class ProvisionFile {
        List<ProvisionGroup> groups;
    }

    @Data
    public static class ProvisionGroup {
        String name;
        String icon;
        String colour;
    }
}
