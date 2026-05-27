package com.workflowos.workspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "file_index_records")
@Getter
@Setter
public class FileIndexRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "workspace_folder_id", nullable = false)
    private Long workspaceFolderId;

    @Column(nullable = false, length = 1024)
    private String path;

    @Column(name = "relative_path", nullable = false, length = 1024)
    private String relativePath;

    @Column(length = 40)
    private String extension;

    @Column(name = "content_hash", length = 128)
    private String contentHash;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    @Column(name = "indexed_at", nullable = false)
    private Instant indexedAt;

    @PrePersist
    @PreUpdate
    void onIndex() {
        indexedAt = Instant.now();
    }
}
