package com.workflowos.auth.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents an active user session.
 * Created on login, deleted on logout or expiry.
 */
@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 512, unique = true)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public UUID getId()                               { return id; }
    public UUID getUserId()                           { return userId; }
    public void setUserId(UUID userId)                { this.userId = userId; }
    public String getToken()                          { return token; }
    public void setToken(String token)                { this.token = token; }
    public OffsetDateTime getExpiresAt()              { return expiresAt; }
    public void setExpiresAt(OffsetDateTime expiresAt){ this.expiresAt = expiresAt; }
    public OffsetDateTime getCreatedAt()              { return createdAt; }

    /** Returns true if this session has not yet expired. */
    public boolean isActive() {
        return OffsetDateTime.now().isBefore(expiresAt);
    }
}
