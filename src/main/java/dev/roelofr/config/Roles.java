package dev.roelofr.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Roles {
    public static final String User = "${app.roles.user}";
    public static final String Admin = "${app.roles.admin}";
    public static final String CentralePost = "${app.roles.centrale-post}";
    public static final String Wijkhouder = "${app.roles.wijkhouder}";
    public static final String Gedelegeerd = "${app.roles.gedelegeerd}";
}
