package dev.roelofr;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Roles {
    public static final String Admin = "${app.roles.admin}";

    public static final String CentralePost = "${app.roles.centrale-post}";

    public static final String WijkHouder = "${app.roles.wijkhouder}";
}
