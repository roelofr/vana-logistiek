package dev.roelofr;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    public static final Locale LocaleDutch = Locale.forLanguageTag("nl-NL");
    public static final ZoneId ZoneIdAmsterdam = ZoneId.of("Europe/Amsterdam");
    public static final String DEFAULT_ADMIN_EMAIL = "admin@example.com";
    public static final String TEAM_DEFAULT = "Default Team";
    public static final String TEAM_CP = "Centrale Post";

}
