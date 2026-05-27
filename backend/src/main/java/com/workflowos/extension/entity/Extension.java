package com.workflowos.extension.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "extensions")
@Getter
@Setter
public class Extension {
    @Id
    @Column(length = 120)
    private String id;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(nullable = false, length = 40)
    private String version;

    @Column(length = 500)
    private String description;

    @Column(name = "publisher_id", length = 120)
    private String publisherId;

    @Column(name = "entrypoint", nullable = false, length = 300)
    private String entrypoint;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "permissions", columnDefinition = "jsonb")
    private List<String> permissions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "manifest", columnDefinition = "jsonb")
    private Map<String, Object> manifest;

    @Column(name = "signature", length = 512)
    private String signature;

    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
