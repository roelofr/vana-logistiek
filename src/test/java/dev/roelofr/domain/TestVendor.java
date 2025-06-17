package dev.roelofr.domain;

import java.util.concurrent.atomic.AtomicLong;

public class TestVendor extends Vendor {
    private static final AtomicLong incremental = new AtomicLong(0L);

    public static Vendor make(Long id, String name, String number, District district) {
        var vendor = new TestVendor();

        vendor.setName(name);
        vendor.setNumber(number);
        vendor.setDistrict(district);
        vendor.setId(id);

        return vendor;
    }

    public static Vendor make(Long id, String name, String number, String district) {
        return make(id, name, number, TestDistrict.make(district));
    }

    public static Vendor make(String name, String number, String district) {
        return make(incremental.incrementAndGet(), name, number, district);
    }

    public void setId(Long id) {
        this.id = id;
    }
}
