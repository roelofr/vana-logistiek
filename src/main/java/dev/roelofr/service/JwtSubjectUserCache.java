package dev.roelofr.service;

import dev.roelofr.domain.User;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class JwtSubjectUserCache {
    final static Duration MAX_AGE = Duration.ofMinutes(1);

    final UserService userService;
    final Map<String, Long> idCache = new HashMap<>();
    final Map<String, Instant> cacheInject = new HashMap<>();

    @Scheduled(every = "5m", delayed = "5m")
    public void cleanCache() {
        final var expirationDate = Instant.now().minus(MAX_AGE);

        for (var key : cacheInject.keySet()) {
            if (expirationDate.isBefore(cacheInject.get(key)))
                continue;

            idCache.remove(key);
            cacheInject.remove(key);
        }

        for (var key : idCache.keySet()) {
            if (!cacheInject.containsKey(key))
                idCache.remove(key);
        }
    }

    public Optional<User> get(JsonWebToken jwt) {
        var subject = jwt.getSubject();
        if (!idCache.containsKey(subject))
            return Optional.empty();

        return userService.findById(idCache.get(subject));

    }

    public void put(JsonWebToken jwt, User user) {
        idCache.put(jwt.getSubject(), user.getId());
        cacheInject.put(jwt.getSubject(), Instant.now());
    }
}
