package dev.roelofr.domain.enums;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateType {
    public static final String Message = "message";
    public static final String Created = "created";
    public static final String Resolved = "resolved";
    public static final String AssignToTeam = "assigned-to-team";
    public static final String ClaimedByUser = "clamed-by-user";
}
