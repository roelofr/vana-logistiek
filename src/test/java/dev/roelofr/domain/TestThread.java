package dev.roelofr.domain;

import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class TestThread extends Thread {
    private static final AtomicLong incremental = new AtomicLong(0L);

    public static TestThread make(Long id, String subject, @Nullable Vendor vendor) {
        TestThread ticket = new TestThread();
        User user = TestUser.make("Test user");

        ticket.setId(id);

        ticket.setVendor(vendor);
        ticket.setSubject(subject);

        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        ticket.setUser(user);
        ticket.setTeam(user.getTeam());

        return ticket;
    }

    public static TestThread make(String description, @Nullable Vendor vendor) {
        return make(incremental.incrementAndGet(), description, vendor);
    }

    public static TestThread make(String description) {
        return make(incremental.incrementAndGet(), description, null);
    }

    private void setId(Long id) {
        this.id = id;
    }

    public TestThread updated() {
        throw new RuntimeException("Deprecated method call");
    }

    public TestThread assigned(Team team) {
        setAssignedTeam(team);
        setAssignedUser(null);
        return this;
    }

    public TestThread assigned(User user) {
        setAssignedTeam(user.getTeam());
        setAssignedUser(user);
        return this;
    }

    public TestThread completed() {
        setResolvedAt(LocalDateTime.now());
        return this;
    }
}
