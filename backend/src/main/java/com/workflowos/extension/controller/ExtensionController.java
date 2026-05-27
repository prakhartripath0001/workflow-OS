package com.workflowos.extension.controller;

import com.workflowos.auth.service.AuthService;
import com.workflowos.common.dto.ApiResponse;
import com.workflowos.entity.User;
import com.workflowos.extension.dto.ExtensionManifestRequest;
import com.workflowos.extension.dto.InstallExtensionRequest;
import com.workflowos.extension.entity.Extension;
import com.workflowos.extension.entity.InstalledExtension;
import com.workflowos.extension.service.ExtensionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/extensions")
@RequiredArgsConstructor
public class ExtensionController {
    private final ExtensionService extensionService;
    private final AuthService authService;

    @GetMapping("/marketplace")
    public ApiResponse<List<Extension>> marketplace() {
        return ApiResponse.ok(extensionService.marketplace());
    }

    @PostMapping("/publish")
    public ApiResponse<Extension> publish(@Valid @RequestBody ExtensionManifestRequest request) {
        return ApiResponse.ok("Extension published", extensionService.publish(request));
    }

    @GetMapping("/installed")
    public ResponseEntity<ApiResponse<List<InstalledExtension>>> installed(@RequestHeader("Authorization") String authHeader) {
        UUID userId = currentUser(authHeader).getId();
        return ResponseEntity.ok(ApiResponse.ok(extensionService.installed(userId)));
    }

    @PostMapping("/install")
    public ResponseEntity<ApiResponse<InstalledExtension>> install(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody InstallExtensionRequest request) {
        UUID userId = currentUser(authHeader).getId();
        return ResponseEntity.ok(ApiResponse.ok("Extension installed", extensionService.install(userId, request.extensionId())));
    }

    @PatchMapping("/{extensionId}/enabled")
    public ResponseEntity<ApiResponse<InstalledExtension>> enabled(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String extensionId,
            @RequestParam boolean enabled) {
        UUID userId = currentUser(authHeader).getId();
        return ResponseEntity.ok(ApiResponse.ok(extensionService.setEnabled(userId, extensionId, enabled)));
    }

    private User currentUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing bearer token");
        }
        return authService.validateSession(authHeader.substring(7));
    }
}
