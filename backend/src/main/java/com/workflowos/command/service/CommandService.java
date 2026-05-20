package com.workflowos.command.service;

import com.workflowos.command.model.CommandExecutionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CommandService {
    CommandExecutionResponse execute(UUID userId, String rawInput);
    List<Map<String, Object>> getAvailableCommands();
    Page<CommandExecutionResponse> getHistory(UUID userId, Pageable pageable);
}
