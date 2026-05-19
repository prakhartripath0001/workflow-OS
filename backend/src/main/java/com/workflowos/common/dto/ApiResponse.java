// =============================================================================
// ApiResponse<T> — Standardized API Response Envelope
// Every REST endpoint returns this wrapper for consistency.
// Clients always know where to find data, errors, and metadata.
// =============================================================================
package com.workflowos.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final String errorCode;
    private final long timestamp;

    private ApiResponse(boolean success, String message, T data, String errorCode) {
        this.success   = success;
        this.message   = message;
        this.data      = data;
        this.errorCode = errorCode;
        this.timestamp = Instant.now().toEpochMilli();
    }

    // ─── Factory Methods ──────────────────────────────────────────────────────

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "Success", data, null);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, "Created successfully", data, null);
    }

    public static ApiResponse<Void> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode);
    }

    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(false, message, null, "INTERNAL_ERROR");
    }
}
