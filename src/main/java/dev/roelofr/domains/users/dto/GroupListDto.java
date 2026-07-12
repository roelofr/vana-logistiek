package dev.roelofr.domains.users.dto;

import dev.roelofr.domains.users.model.Group;

public record GroupListDto(
    long id,
    String name,
    String colour,
    String icon
) {
    public GroupListDto(Group group) {
        this(group.getId(), group.getName(), group.getColour(), group.getIcon());
    }
}
