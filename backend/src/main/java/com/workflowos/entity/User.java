package com.workflowos.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    public enum Provider { local, google, github }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @JsonIgnore
    @Column(name = "password_hash", length = 255)   // nullable — OAuth users have no password
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "auth_provider")
    private Provider provider = Provider.local;

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────────
    public UUID getId()                              { return id; }
    public String getName()                          { return name; }
    public void   setName(String name)               { this.name = name; }
    public String getEmail()                         { return email; }
    public void   setEmail(String email)             { this.email = email; }
    public String getPasswordHash()                  { return passwordHash; }
    public void   setPasswordHash(String hash)       { this.passwordHash = hash; }
    public Provider getProvider()                    { return provider; }
    public void   setProvider(Provider provider)     { this.provider = provider; }
    public String getProviderId()                    { return providerId; }
    public void   setProviderId(String providerId)   { this.providerId = providerId; }
    public String getAvatarUrl()                     { return avatarUrl; }
    public void   setAvatarUrl(String avatarUrl)     { this.avatarUrl = avatarUrl; }
    public OffsetDateTime getCreatedAt()             { return createdAt; }
    public OffsetDateTime getUpdatedAt()             { return updatedAt; }
}
