package com.workflowos.command.controller;

import com.workflowos.auth.service.AuthService;
import com.workflowos.command.model.CommandExecutionRequest;
import com.workflowos.command.model.CommandExecutionResponse;
import com.workflowos.command.service.CommandService;
import com.workflowos.common.dto.ApiResponse;
import com.workflowos.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/commands")
@RequiredArgsConstructor
@Tag(name = "Commands", description = "Slash command execution and history")
@SecurityRequirement(name = "bearerAuth")
public class CommandController {

    private final CommandService commandService;
    private final AuthService authService;

    @Operation(summary = "Execute a slash command",
               description = "Parses the raw input string, routes to the appropriate command handler, and returns the result.")
    @PostMapping("/execute")
    public ResponseEntity<ApiResponse<CommandExecutionResponse>> execute(
            @Valid @RequestBody CommandExecutionRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Missing or invalid Authorization header", "UNAUTHORIZED"));
        }
        String token = authHeader.substring(7);
        User user = authService.validateSession(token);
        UUID userId = user.getId();

        log.info("[Command] User {} executing: {}", userId, request.getRawInput());
        CommandExecutionResponse result = commandService.execute(userId, request.getRawInput());
        return ResponseEntity.ok(ApiResponse.ok("Command executed", result));
    }

    @Operation(summary = "List all available commands")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listCommands() {
        return ResponseEntity.ok(ApiResponse.ok(commandService.getAvailableCommands()));
    }

    @Operation(summary = "Get command execution history for the authenticated user")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<CommandExecutionResponse>>> history(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Missing or invalid Authorization header", "UNAUTHORIZED"));
        }
        String token = authHeader.substring(7);
        User user = authService.validateSession(token);
        UUID userId = user.getId();

        Page<CommandExecutionResponse> history =
                commandService.getHistory(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.ok(history));
    }
}
