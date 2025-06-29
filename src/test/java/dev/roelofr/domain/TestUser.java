package dev.roelofr.domain;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.concurrent.atomic.AtomicLong;

public class TestUser extends User {
    private static final AtomicLong incremental = new AtomicLong(0L);

    public static User make(Long id, String name, @Nullable District district) {
        var vendor = new TestUser();

        vendor.setId(id);
        vendor.setName(name);
        vendor.setEmail(name.toLowerCase().replaceAll("[^a-z0-9]", ".") + "@example.com");
        vendor.setDistrict(district);

        return vendor;
    }

    public static User make(Long id, String name, @Nonnull String district) {
        return make(id, name, TestDistrict.make(district));
    }

    public static User make(String name, @Nonnull String district) {
        return make(incremental.incrementAndGet(), name, district);
    }

    public static User make(String name, @Nonnull District district) {
        return make(incremental.incrementAndGet(), name, district);
    }

    public static User make(String name) {
        return make(incremental.incrementAndGet(), name, (District) null);
    }

    public void setId(Long id) {
        this.id = id;
    }
}
