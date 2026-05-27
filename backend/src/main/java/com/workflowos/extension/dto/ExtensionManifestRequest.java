package com.workflowos.extension.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

public record ExtensionManifestRequest(
        @NotBlank String id,
        @NotBlank String name,
        @NotBlank String version,
        String description,
        String publisherId,
        @NotBlank String entrypoint,
        @NotEmpty List<String> permissions,
        Map<String, Object> manifest,
        String signature
) {}
