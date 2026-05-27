package com.workflowos.workspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workspace_folders")
@Getter
@Setter
public class WorkspaceFolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 1024)
    private String path;

    @Column(nullable = false, length = 80)
    private String scope = "read";

    @Column(name = "granted_at", nullable = false, updatable = false)
    private Instant grantedAt;

    @PrePersist
    void onCreate() {
        grantedAt = Instant.now();
    }
}
