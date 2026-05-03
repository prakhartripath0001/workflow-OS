package com.workflowos.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workflows")
public class Workflow {

    public enum Status { DRAFT, ACTIVE, PAUSED, COMPLETED, ARCHIVED }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "workflow_status")
    private Status status = Status.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<WorkflowTask> tasks;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = updatedAt = OffsetDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = OffsetDateTime.now(); }

    // ── Getters & Setters ──────────────────────────────────────────────────
    public UUID          getId()                         { return id; }
    public String        getName()                       { return name; }
    public void          setName(String name)            { this.name = name; }
    public String        getDescription()                { return description; }
    public void          setDescription(String d)        { this.description = d; }
    public Status        getStatus()                     { return status; }
    public void          setStatus(Status status)        { this.status = status; }
    public User          getOwner()                      { return owner; }
    public void          setOwner(User owner)            { this.owner = owner; }
    public List<WorkflowTask> getTasks()                 { return tasks; }
    public OffsetDateTime    getCreatedAt()              { return createdAt; }
    public OffsetDateTime    getUpdatedAt()              { return updatedAt; }
}
