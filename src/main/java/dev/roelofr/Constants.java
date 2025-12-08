package dev.roelofr;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    public static final Locale LocaleDutch = Locale.forLanguageTag("dut");

    public static final String ROLE_ADMIN = "u_admin";

    public static final ZoneId ZoneIdAmsterdam = ZoneId.of("Europe/Amsterdam");

    public static final String DEFAULT_ADMIN_EMAIL = "admin@example.com";

}
