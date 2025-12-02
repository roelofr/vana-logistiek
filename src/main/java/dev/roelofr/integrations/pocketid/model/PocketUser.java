package dev.roelofr.integrations.pocketid.model;

import java.util.List;

public record PocketUser(
    String id,
    String username,
    String email,
    String firstName,
    String lastName,
    String displayName,
    boolean isAdmin,
    String locale,
    List<CustomClaim> customClaims,
    List<UserGroup> userGroups,
    boolean disabled
) {
    @Override
    public String toString() {
        return String.format("User %s (%s)", id, displayName);
    }

    record CustomClaim(
        String key,
        String value
    ) {
        @Override
        public String toString() {
            return String.format("{%s = %s}", key, value);
        }
    }

    record UserGroup(
        String id,
        String friendlyName,
        String name,
        List<CustomClaim> customClaims
    ) {
        @Override
        public String toString() {
            return name;
        }
    }
}
