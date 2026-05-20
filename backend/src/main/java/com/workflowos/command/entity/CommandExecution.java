package com.workflowos.command.entity;

import com.workflowos.ai.model.ParsedIntent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "command_executions")
@Getter
@Setter
public class CommandExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "command_name", nullable = false, length = 100)
    private String commandName;

    @Column(name = "raw_input", nullable = false, columnDefinition = "text")
    private String rawInput;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parsed_intent", columnDefinition = "jsonb")
    private ParsedIntent parsedIntent;

    @Column(nullable = false, length = 50)
    private String status = "PENDING"; // PENDING | SUCCESS | FAILED

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "result", columnDefinition = "jsonb")
    private Object result;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "execution_ms")
    private Integer executionMs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
