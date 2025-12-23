package dev.roelofr;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Roles {
    /**
     * Admin users
     */
    public static final String Admin = "${app.roles.admin}";

    /**
     * People allowed to re-distribute threads.
     */
    public static final String CentralePost = "${app.roles.centrale-post}";

    /**
     * People in the field that can create tickets.
     */
    public static final String Wijkhouder = "${app.roles.wijkhouder}";

    /**
     * People who are only to see their own tickets.
     */
    public static final String Gedelegeerd = "${app.roles.gedelegeerd}";
}
