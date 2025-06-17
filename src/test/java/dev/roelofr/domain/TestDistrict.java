package dev.roelofr.domain;

import java.util.concurrent.atomic.AtomicLong;

import static dev.roelofr.StringUtil.mobileName;

public class TestDistrict extends District {
    private static final AtomicLong incremental = new AtomicLong(0L);

    public static District make(Long id, String name) {
        var district = new TestDistrict();

        district.setId(id);
        district.setName(name);
        district.setMobileName(mobileName(name));

        return district;
    }

    public static District make(String name) {
        return make(incremental.incrementAndGet(), name);
    }

    private void setId(Long id) {
        this.id = id;
    }
}
