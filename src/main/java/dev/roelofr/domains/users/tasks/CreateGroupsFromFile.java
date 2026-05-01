package dev.roelofr.domains.users.tasks;

import dev.roelofr.domains.users.GroupService;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.GroupRepository;
import dev.roelofr.jobs.Priorities;
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
    private final GroupService groupService;
    private final GroupRepository groupRepository;

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
                    log.warn("Found existing group [{}] to meet required group [{}], but it is not a system group!",
                        existingGroup.getName(), provisionGroup.getName());

                log.info("Required group [{}] exists as [{}]", provisionGroup.getName(), existingGroup.getName());
                continue;
            }

            var group = Group.builder()
                .name(provisionGroup.getName())
                .label(provisionGroup.getName())
                .system(true)
                .build();

            log.info("Created system group [{}]", group.getName());

            groupRepository.persist(group);
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
