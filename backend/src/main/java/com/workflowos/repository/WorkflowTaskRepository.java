package com.workflowos.repository;

import com.workflowos.entity.WorkflowTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkflowTaskRepository extends JpaRepository<WorkflowTask, UUID> {
    List<WorkflowTask> findByWorkflowIdOrderByPositionAsc(UUID workflowId);
    List<WorkflowTask> findByStatus(WorkflowTask.Status status);
}
