package dev.roelofr.domain;

import java.util.concurrent.atomic.AtomicLong;

public class TestTeam extends Team {
    private static final AtomicLong incremental = new AtomicLong(0L);

    public static Team make(Long id, String name) {
        var district = new TestTeam();

        district.setId(id);
        district.setName(name);

        return district;
    }

    public static Team make(String name) {
        return make(incremental.incrementAndGet(), name);
    }

    private void setId(Long id) {
        this.id = id;
    }
}
