package com.workflowos.workspace.service;

import com.workflowos.workspace.dto.FileIndexRecordRequest;
import com.workflowos.workspace.dto.GrantWorkspaceRequest;
import com.workflowos.workspace.entity.FileIndexRecord;
import com.workflowos.workspace.entity.WorkspaceFolder;

import java.util.List;
import java.util.UUID;

public interface WorkspaceService {
    WorkspaceFolder grant(UUID userId, GrantWorkspaceRequest request);
    List<WorkspaceFolder> folders(UUID userId);
    FileIndexRecord upsertIndexRecord(UUID userId, FileIndexRecordRequest request);
    List<FileIndexRecord> index(UUID userId, Long workspaceFolderId);
}
