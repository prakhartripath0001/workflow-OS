package com.workflowos.repository;

import com.workflowos.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {
    List<Workflow> findByOwnerId(UUID ownerId);
    List<Workflow> findByStatus(Workflow.Status status);
}
