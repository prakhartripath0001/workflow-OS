package com.workflowos.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record FileIndexRecordRequest(
        @NotNull Long workspaceFolderId,
        @NotBlank String path,
        @NotBlank String relativePath,
        String extension,
        String contentHash,
        Long sizeBytes,
        Instant modifiedAt
) {}
