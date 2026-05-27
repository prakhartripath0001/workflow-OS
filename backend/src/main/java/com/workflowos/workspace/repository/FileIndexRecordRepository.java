package com.workflowos.workspace.repository;

import com.workflowos.workspace.entity.FileIndexRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileIndexRecordRepository extends JpaRepository<FileIndexRecord, Long> {
    List<FileIndexRecord> findByUserIdAndWorkspaceFolderId(UUID userId, Long workspaceFolderId);
}
