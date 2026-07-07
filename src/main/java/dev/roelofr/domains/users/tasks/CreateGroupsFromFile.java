package dev.roelofr.domains.users.tasks;

import dev.roelofr.domains.users.GroupService;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.GroupRepository;
import dev.roelofr.jobs.Priorities;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.util.StringUtil;
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
            var groupName = provisionGroup.getName();
            var groupLabel = provisionGroup.getLabel();
            var groupIcon = provisionGroup.getIcon();
            var groupColour = provisionGroup.getColour();

            if (groupLabel == null)
                groupLabel = groupName.toLowerCase().replaceAll("[^a-z0-9]+", "-");

            var existingGroup = groupService.findByName(groupName).orElse(null);
            if (existingGroup != null) {
                if (!existingGroup.isSystem())
                    log.warn("Found existing group [{}] to meet required group [{}], but it is not a system group!",
                        existingGroup.getName(), groupName);

                log.info("Required group [{}] exists as [{}]", groupName, existingGroup.getName());

                if (!groupLabel.equalsIgnoreCase(existingGroup.getLabel())) {
                    log.info("Updating label of existing group [{}] to [{}]", existingGroup.getName(), groupLabel);
                    existingGroup.setLabel(groupLabel);
                }

                if (StringUtil.isNullOrEmpty(existingGroup.getIcon()) && !StringUtil.isNullOrEmpty(groupIcon))
                    existingGroup.setIcon(groupIcon);

                if (StringUtil.isNullOrEmpty(existingGroup.getColour()) && !StringUtil.isNullOrEmpty(groupColour))
                    existingGroup.setColour(groupColour);

                continue;
            }

            var group = Group.builder()
                .name(groupName)
                .label(groupLabel)
                .icon(groupIcon)
                .colour(groupColour)
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
        String label;
    }
}
