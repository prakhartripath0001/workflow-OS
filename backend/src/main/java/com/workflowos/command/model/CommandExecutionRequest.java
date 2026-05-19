// =============================================================================
// CommandExecutionRequest DTO
// =============================================================================
package com.workflowos.command.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommandExecutionRequest {
    @NotBlank(message = "Command input must not be blank")
    @Size(max = 2000, message = "Command input must not exceed 2000 characters")
    private String rawInput;
}
