package dev.roelofr;

import java.time.ZoneId;
import java.util.Locale;

public final class Constants {
    public static final Locale LocaleDutch = Locale.forLanguageTag("dut");

    public static final String ROLE_ADMIN = "u_admin";

    public static final ZoneId ZoneIdAmsterdam = ZoneId.of("Europe/Amsterdam");

    public static final String DEFAULT_ADMIN_EMAIL = "admin@example.com";

    private Constants() {
    }
}
