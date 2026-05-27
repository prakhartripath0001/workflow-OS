package com.workflowos.extension.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "installed_extensions",
       uniqueConstraints = @UniqueConstraint(name = "uq_installed_extension_user", columnNames = {"user_id", "extension_id"}))
@Getter
@Setter
public class InstalledExtension {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "extension_id", nullable = false, length = 120)
    private String extensionId;

    @Column(nullable = false, length = 40)
    private String version;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "installed_at", nullable = false, updatable = false)
    private Instant installedAt;

    @PrePersist
    void onCreate() {
        installedAt = Instant.now();
    }
}
