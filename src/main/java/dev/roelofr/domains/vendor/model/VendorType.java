package dev.roelofr.domains.vendor.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VendorType {
    Food("i-lucide-cooking-pot"),
    Entertainment("i-lucide-drama"),
    Shop("i-lucide-store");

    public final String icon;

    @JsonValue
    public String jsonName() {
        return name().toLowerCase();
    }
}
