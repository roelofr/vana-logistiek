package dev.roelofr.jobs;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Priorities {
    static final int Provision = 100;
    static final int Repair = 400;
    static final int Develop = 450;
    static final int Download = 900;

}
