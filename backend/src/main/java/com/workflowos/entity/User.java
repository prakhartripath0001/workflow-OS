package com.workflowos.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = OffsetDateTime.now();
        if (passwordHash == null) passwordHash = "";
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
    public String getAvatarUrl()                     { return avatarUrl; }
    public void   setAvatarUrl(String avatarUrl)     { this.avatarUrl = avatarUrl; }
    public OffsetDateTime getCreatedAt()             { return createdAt; }
    public OffsetDateTime getUpdatedAt()             { return updatedAt; }
}

