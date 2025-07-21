package dev.roelofr.domain;

import dev.roelofr.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static dev.roelofr.DomainHelper.EMAIL_ADMIN;
import static dev.roelofr.DomainHelper.EMAIL_USER;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
class UserTest {
    @Inject
    UserRepository userRepository;

    @Test
    @Disabled("I am making the wrong assumptions")
    @TestSecurity(user = EMAIL_USER)
    void getEmailAsUser() {
        var userOptional = userRepository.streamAll().findFirst();
        Assumptions.assumeTrue(userOptional.isPresent());

        var user = userOptional.get();

        assertNull(user.getEmail());
    }

    @Test
    @TestSecurity(user = EMAIL_ADMIN)
    void getEmailAsAdmin() {
        var userOptional = userRepository.streamAll().findFirst();
        Assumptions.assumeTrue(userOptional.isPresent());

        var user = userOptional.get();

        assertNotNull(user.getEmail());
    }
}
