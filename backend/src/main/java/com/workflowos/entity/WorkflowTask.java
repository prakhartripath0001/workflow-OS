package com.workflowos.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_tasks")
public class WorkflowTask {

    public enum Status { TODO, IN_PROGRESS, BLOCKED, DONE, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "task_status")
    private Status status = Status.TODO;

    @Column(nullable = false)
    private int position;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = updatedAt = OffsetDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = OffsetDateTime.now(); }

    // ── Getters & Setters ──────────────────────────────────────────────────
    public UUID         getId()                         { return id; }
    public Workflow     getWorkflow()                   { return workflow; }
    public void         setWorkflow(Workflow w)         { this.workflow = w; }
    public String       getTitle()                      { return title; }
    public void         setTitle(String title)          { this.title = title; }
    public String       getDescription()                { return description; }
    public void         setDescription(String d)        { this.description = d; }
    public Status       getStatus()                     { return status; }
    public void         setStatus(Status status)        { this.status = status; }
    public int          getPosition()                   { return position; }
    public void         setPosition(int position)       { this.position = position; }
    public OffsetDateTime getCreatedAt()                { return createdAt; }
    public OffsetDateTime getUpdatedAt()                { return updatedAt; }
}
