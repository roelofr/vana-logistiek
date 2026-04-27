package dev.roelofr.domains.users;

import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.GroupRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class GroupService {
    final GroupRepository groupRepository;

    public List<Group> listAll() {
        return groupRepository.listAll(
            Sort.by("name")
                .and("id")
        );
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
}
