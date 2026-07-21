package dev.roelofr.domains.users;

import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.GroupRepository;
import dev.roelofr.domains.users.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class GroupService {
    final GroupRepository groupRepository;

    public List<Group> listAll() {
        return groupRepository.listAllSorted();
    }

    public List<Group> listAllWithGroup() {
        return groupRepository.listAllWithGroupsSorted();
    }

    public Optional<Group> findByName(String name) {
        return groupRepository.findByName(name);
    }

    public Optional<Group> findByLabel(String label) {
        return groupRepository.find("LOWER(label) = LOWER(?1)", label).singleResultOptional();
    }

    public Group findByLabelOrFail(String label) {
        return findByLabel(label).orElseThrow(() -> new RuntimeException(String.format(
            "Failed to find group with label [%s]",
            label
        )));
    }

    public Optional<Group> findGroupLike(String name) {
        return groupRepository.find("#Group.getLikeName", name).firstResultOptional();
    }

    @Transactional
    public Group createGroup(String name) {
        var group = Group.builder()
            .name(name)
            .build();

        groupRepository.persist(group);

        return group;
    }

    public Group findById(long groupId) {
        return groupRepository.findById(groupId);
    }

    public List<Group> findByIds(@NotNull List<Long> groups) {
        return groupRepository.list("id IN ?1", groups);
    }

    public List<Group> findByUser(User user) {
        return groupRepository.list("#Group.findByUserIdWithDistrict", Map.of("user", user));
    }

    public void save(Group newGroup) {
        if (newGroup.getId() != null)
            throw new IllegalArgumentException("Cannot save already saved object");

        groupRepository.persist(newGroup);
    }
}
