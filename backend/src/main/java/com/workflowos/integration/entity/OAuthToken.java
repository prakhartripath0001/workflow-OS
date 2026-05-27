package com.workflowos.integration.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "oauth_tokens",
       uniqueConstraints = @UniqueConstraint(name = "uq_oauth_user_provider", columnNames = {"user_id", "provider"}))
@Getter
@Setter
public class OAuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 80)
    private String provider;

    @Column(name = "access_token_ciphertext", nullable = false, columnDefinition = "text")
    private String accessTokenCiphertext;

    @Column(name = "refresh_token_ciphertext", columnDefinition = "text")
    private String refreshTokenCiphertext;

    @Column(name = "scope", length = 1000)
    private String scope;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
