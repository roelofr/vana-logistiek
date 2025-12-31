package dev.roelofr.domain.enums;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UpdateType {
    Message,
    Created,
    Resolved,
    AssignToTeam,
    ClaimedByUser;

    /**
     * Used in ORM, do not use outside domain!
     */
    public static class Types {
        public static final String Message = "Message";
        public static final String Created = "Created";
        public static final String Resolved = "Resolved";
        public static final String AssignToTeam = "AssignToTeam";
        public static final String ClaimedByUser = "ClaimedByUser";
    }
}
