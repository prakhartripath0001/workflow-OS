package com.workflowos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status",    "UP",
            "service",   "workflow-os-backend",
            "version",   "1.0.0",
            "timestamp", LocalDateTime.now().toString()
        ));
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
            "app",         "Workflow OS",
            "description", "Cross-platform workflow automation desktop app",
            "stack",       Map.of(
                "backend",  "Spring Boot 3.2 / Java 17",
                "frontend", "Electron + React 18 + Vite + Tailwind CSS"
            )
        ));
    }
}
