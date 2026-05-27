package com.workflowos.workspace.repository;

import com.workflowos.workspace.entity.WorkspaceFolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkspaceFolderRepository extends JpaRepository<WorkspaceFolder, Long> {
    List<WorkspaceFolder> findByUserId(UUID userId);
}
