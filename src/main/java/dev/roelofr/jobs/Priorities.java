package dev.roelofr.jobs;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Priorities {
    public static final int Provision = 100;
    public static final int Repair = 400;
    public static final int Develop = 450;
    public static final int Download = 900;

}
