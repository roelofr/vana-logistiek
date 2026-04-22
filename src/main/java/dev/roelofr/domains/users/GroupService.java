package dev.roelofr.domains.users;

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

    public Optional<Group> findGroup(String name) {
        return groupRepository.findByName(name);
    }
}
