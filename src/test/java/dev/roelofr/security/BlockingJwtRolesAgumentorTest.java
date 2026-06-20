package dev.roelofr.security;

import dev.roelofr.config.AppConfig;
import dev.roelofr.domains.users.UserService;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.service.JwtSubjectUserCache;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class BlockingJwtRolesAgumentorTest {
    @Mock
    UserService userService;

    @Mock
    JwtSubjectUserCache jwtSubjectUserCache;

    @Mock
    AppConfig appConfig;

    @InjectMocks
    BlockingJwtRolesAgumentor blockingJwtRolesAgumentor;

    public static List<Arguments> getNameFromJwtSource() {
        return List.of(
            Arguments.argumentSet("full_name", new MappedJwt(Claims.full_name, "Stefan Test"), "Stefan Test"),
            Arguments.argumentSet("given", new MappedJwt(Claims.given_name, "Stefan"), "Stefan"),
            Arguments.argumentSet("family", new MappedJwt(Claims.family_name, "Test"), "Test"),
            Arguments.argumentSet("given + family", new MappedJwt(Map.ofEntries(
                Map.entry(Claims.given_name, "Stefan"),
                Map.entry(Claims.middle_name, "Testius"),
                Map.entry(Claims.family_name, "Test")
            )), "Stefan Testius Test"),
            Arguments.argumentSet("email", new MappedJwt(Claims.email, "stefan.test@example.com"), "Stefan Test"),
            Arguments.argumentSet("subject", new MappedJwt(Claims.sub, "Spinal Tap"), "Spinal Tap"),
            Arguments.argumentSet("token name", new MappedJwt("helloworld", Map.of()), "helloworld")
        );
    }

    @Test
    void augment() {
    }

    @Test
    void augmentJwt() {
    }

    @Test
    void getOrCreateUserFromJwtFreshUser() {
        var token = new MappedJwt(Map.ofEntries(
            Map.entry(Claims.sub, "1234567890"),
            Map.entry(Claims.full_name, "Test User"),
            Map.entry(Claims.email, "hello@example.com"),
            Map.entry(Claims.email_verified, true),
            Map.entry(Claims.groups, Set.of("alpha", "bravo"))
        ));

        // Ensure it is never found
        BDDMockito.given(jwtSubjectUserCache.get(token)).willReturn(Optional.empty());
        BDDMockito.given(userService.findByEmail(token.getClaim(Claims.email))).willReturn(Optional.empty());

        // WHEN
        var result = blockingJwtRolesAgumentor.getOrCreateUserFromJwt(token);

        // Then
        assertNotNull(result);
        assertNotNull(result.getProviderId());
        assertEquals(token.getClaim(Claims.full_name), result.getName());
        assertEquals(token.getClaim(Claims.email), result.getEmail());
        assertIterableEquals(token.getGroups(), result.getRoles());

        BDDMockito.then(userService).should().save(result);
    }


    @Test
    void getOrCreateUserFromJwtEmailExists() {
        var token = new MappedJwt(Map.ofEntries(
            Map.entry(Claims.sub, "1234567890"),
            Map.entry(Claims.full_name, "Test User"),
            Map.entry(Claims.email, "hello@example.com"),
            Map.entry(Claims.email_verified, true),
            Map.entry(Claims.groups, Set.of("alpha", "bravo"))
        ));

        // User to update
        var emailUser = User.builder()
            .email(token.getClaim(Claims.email))
            .roles(new ArrayList<>(List.of("charlie")))
            .build();

        // Ensure it is never found
        BDDMockito.given(jwtSubjectUserCache.get(token)).willReturn(Optional.empty());
        BDDMockito.given(userService.findByEmail(token.getClaim(Claims.email))).willReturn(Optional.of(emailUser));

        // WHEN
        var result = blockingJwtRolesAgumentor.getOrCreateUserFromJwt(token);

        // Then
        assertSame(emailUser, result);
        assertNotNull(result.getProviderId());
        assertTrue(result.getRoles().containsAll(List.of("charlie", "alpha", "bravo")));

        // Other values should not have been changed
        assertNull(result.getName());
        assertEquals(token.getClaim(Claims.email), result.getEmail());

        BDDMockito.then(userService).should(Mockito.never()).save(result);
    }

    @ParameterizedTest
    @MethodSource("getNameFromJwtSource")
    void getNameFromJwt(JsonWebToken token, String expectedName) {
        assertEquals(expectedName, blockingJwtRolesAgumentor.getNameFromJwt(token));
    }

    record MappedJwt(String name, Map<Claims, Object> claim) implements JsonWebToken {
        public MappedJwt(Map<Claims, Object> claim) {
            this((String) null, claim);
        }

        public MappedJwt(Claims claim, Object value) {
            this((String) null, Map.of(claim, value));
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Set<String> getClaimNames() {
            return claim.keySet().stream().map(Enum::name).collect(Collectors.toSet());
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getClaim(String claimName) {
            return (T) claim.get(Claims.valueOf(claimName));
        }

        @Override
        @SuppressWarnings("unchecked")
        public Set<String> getGroups() {
            var groupClaim = claim.getOrDefault(Claims.groups, null);
            if (groupClaim == null)
                return Set.of();

            return (Set<String>) groupClaim;
        }
    }
}
