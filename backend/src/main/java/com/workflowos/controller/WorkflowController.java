package com.workflowos.controller;

import com.workflowos.entity.Workflow;
import com.workflowos.entity.WorkflowTask;
import com.workflowos.repository.WorkflowRepository;
import com.workflowos.repository.WorkflowTaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowRepository     workflowRepo;
    private final WorkflowTaskRepository taskRepo;

    public WorkflowController(WorkflowRepository workflowRepo,
                              WorkflowTaskRepository taskRepo) {
        this.workflowRepo = workflowRepo;
        this.taskRepo     = taskRepo;
    }

    /** GET /api/workflows — list all workflows */
    @GetMapping
    public List<Workflow> listAll() {
        return workflowRepo.findAll();
    }

    /** GET /api/workflows/{id} — get a single workflow */
    @GetMapping("/{id}")
    public ResponseEntity<Workflow> getById(@PathVariable UUID id) {
        return workflowRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/workflows/{id}/tasks — list tasks for a workflow */
    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<WorkflowTask>> getTasks(@PathVariable UUID id) {
        if (!workflowRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskRepo.findByWorkflowIdOrderByPositionAsc(id));
    }

    /** POST /api/workflows — create a new workflow */
    @PostMapping
    public ResponseEntity<Workflow> create(@RequestBody Workflow workflow) {
        Workflow saved = workflowRepo.save(workflow);
        return ResponseEntity.status(201).body(saved);
    }

    /** DELETE /api/workflows/{id} — delete a workflow */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!workflowRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        workflowRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
