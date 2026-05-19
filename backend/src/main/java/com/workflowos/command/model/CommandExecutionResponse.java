// =============================================================================
// CommandExecutionResponse DTO
// =============================================================================
package com.workflowos.command.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CommandExecutionResponse {
    private Long id;
    private String commandName;
    private String rawInput;
    private String status;          // PENDING | SUCCESS | FAILED
    private Object result;          // Flexible result payload
    private String errorMessage;
    private Integer executionMs;
    private Instant executedAt;
}
