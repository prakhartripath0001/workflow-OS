package com.workflowos.workspace.controller;

import com.workflowos.auth.service.AuthService;
import com.workflowos.common.dto.ApiResponse;
import com.workflowos.entity.User;
import com.workflowos.workspace.dto.FileIndexRecordRequest;
import com.workflowos.workspace.dto.GrantWorkspaceRequest;
import com.workflowos.workspace.entity.FileIndexRecord;
import com.workflowos.workspace.entity.WorkspaceFolder;
import com.workflowos.workspace.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {
    private final AuthService authService;
    private final WorkspaceService workspaceService;

    @PostMapping("/folders")
    public ApiResponse<WorkspaceFolder> grant(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody GrantWorkspaceRequest request) {
        return ApiResponse.ok("Workspace folder granted", workspaceService.grant(currentUser(authHeader), request));
    }

    @GetMapping("/folders")
    public ApiResponse<List<WorkspaceFolder>> folders(@RequestHeader("Authorization") String authHeader) {
        return ApiResponse.ok(workspaceService.folders(currentUser(authHeader)));
    }

    @PostMapping("/index-records")
    public ApiResponse<FileIndexRecord> indexRecord(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody FileIndexRecordRequest request) {
        return ApiResponse.ok(workspaceService.upsertIndexRecord(currentUser(authHeader), request));
    }

    @GetMapping("/folders/{folderId}/index")
    public ApiResponse<List<FileIndexRecord>> index(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long folderId) {
        return ApiResponse.ok(workspaceService.index(currentUser(authHeader), folderId));
    }

    private UUID currentUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing bearer token");
        }
        User user = authService.validateSession(authHeader.substring(7));
        return user.getId();
    }
}
