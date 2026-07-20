package dev.roelofr.domains.users.jpa;

import dev.roelofr.domains.users.model.GroupFlags;

public class GroupFlagConverter extends AbstractEnumListConverter<GroupFlags> {

    @Override
    protected Class<GroupFlags> getEnumClass() {
        return GroupFlags.class;
    }
}
