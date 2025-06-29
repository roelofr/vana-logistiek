package dev.roelofr.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Roles {
    public static final String Admin = "role.admin";
    public static final String User = "role.user";
    public static final String CentralePost = "role.cp";
}
