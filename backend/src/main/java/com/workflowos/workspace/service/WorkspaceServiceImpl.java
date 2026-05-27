package com.workflowos.workspace.service;

import com.workflowos.workspace.dto.FileIndexRecordRequest;
import com.workflowos.workspace.dto.GrantWorkspaceRequest;
import com.workflowos.workspace.entity.FileIndexRecord;
import com.workflowos.workspace.entity.WorkspaceFolder;
import com.workflowos.workspace.repository.FileIndexRecordRepository;
import com.workflowos.workspace.repository.WorkspaceFolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {
    private final WorkspaceFolderRepository workspaceFolderRepository;
    private final FileIndexRecordRepository fileIndexRecordRepository;

    @Override
    @Transactional
    public WorkspaceFolder grant(UUID userId, GrantWorkspaceRequest request) {
        WorkspaceFolder folder = new WorkspaceFolder();
        folder.setUserId(userId);
        folder.setPath(request.path());
        folder.setScope(request.scope() == null ? "read" : request.scope());
        return workspaceFolderRepository.save(folder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkspaceFolder> folders(UUID userId) {
        return workspaceFolderRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public FileIndexRecord upsertIndexRecord(UUID userId, FileIndexRecordRequest request) {
        FileIndexRecord record = new FileIndexRecord();
        record.setUserId(userId);
        record.setWorkspaceFolderId(request.workspaceFolderId());
        record.setPath(request.path());
        record.setRelativePath(request.relativePath());
        record.setExtension(request.extension());
        record.setContentHash(request.contentHash());
        record.setSizeBytes(request.sizeBytes());
        record.setModifiedAt(request.modifiedAt());
        return fileIndexRecordRepository.save(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileIndexRecord> index(UUID userId, Long workspaceFolderId) {
        return fileIndexRecordRepository.findByUserIdAndWorkspaceFolderId(userId, workspaceFolderId);
    }
}
