package dev.roelofr.domains.users.dto;

import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.users.model.UserFlags;
import dev.roelofr.domains.vendor.dto.DistrictDto;

import java.util.List;

public record SummaryGroupDto(
    long id,
    String name,
    String icon,
    String colour,
    List<DistrictDto> districts,
    List<GroupUserDto> users
) {
    public SummaryGroupDto(Group group, List<User> users) {
        this(
            group.getId(),
            group.getName(),
            group.getIcon(),
            group.getColour(),
            group.getDistricts().stream().map(DistrictDto::new).toList(),
            users.stream().map(GroupUserDto::new).toList()
        );
    }

    public record GroupUserDto(
        long id,
        String name,
        String avatar,
        Boolean atWork
    ) {
        public GroupUserDto(User user) {
            this(user.getId(), user.getName(), user.getAvatar(), user.hasFlag(UserFlags.Active));
        }
    }
}
