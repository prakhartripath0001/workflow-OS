package com.workflowos.workspace.dto;

import jakarta.validation.constraints.NotBlank;

public record GrantWorkspaceRequest(@NotBlank String path, String scope) {
}
