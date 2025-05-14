package dev.roelofr.repository;

import dev.roelofr.domain.WebauthnCredential;

public class WebauthnCredentialRepository implements PanacheRepository<WebauthnCredential> {
    public List<WebAuthnCredential> findByUsername(String username) {
        return list("user.username", username);
    }

    public WebAuthnCredential findByCredentialId(String credentialId) {
        return findById(credentialId);
    }
}
