package dev.roelofr.domains.users.jpa;

import dev.roelofr.domains.users.model.UserFlags;

public class UserFlagConverter extends AbstractEnumListConverter<UserFlags> {

    @Override
    protected Class<UserFlags> getEnumClass() {
        return UserFlags.class;
    }
}
