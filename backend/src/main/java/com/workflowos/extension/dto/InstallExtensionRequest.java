package com.workflowos.extension.dto;

import jakarta.validation.constraints.NotBlank;

public record InstallExtensionRequest(@NotBlank String extensionId) {
}
