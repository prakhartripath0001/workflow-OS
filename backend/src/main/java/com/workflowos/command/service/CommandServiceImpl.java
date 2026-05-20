package com.workflowos.command.service;

import com.workflowos.ai.model.ParsedIntent;
import com.workflowos.ai.service.IntentRecognitionService;
import com.workflowos.command.entity.Command;
import com.workflowos.command.entity.CommandExecution;
import com.workflowos.command.model.CommandExecutionResponse;
import com.workflowos.command.repository.CommandExecutionRepository;
import com.workflowos.command.repository.CommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandServiceImpl implements CommandService {

    private final CommandRepository commandRepo;
    private final CommandExecutionRepository executionRepo;
    private final IntentRecognitionService intentService;

    @Override
    @Transactional
    public CommandExecutionResponse execute(UUID userId, String rawInput) {
        long startTime = System.currentTimeMillis();
        
        // 1. Parse intent using IntentRecognitionService
        ParsedIntent intent = intentService.parse(rawInput);
        String commandName = intent.getCommand();

        CommandExecution execution = new CommandExecution();
        execution.setUserId(userId);
        execution.setCommandName(commandName);
        execution.setRawInput(rawInput);
        execution.setParsedIntent(intent);
        
        try {
            // 2. Validate command exists and is active
            Optional<Command> optCommand = commandRepo.findByName(commandName);
            if (optCommand.isEmpty()) {
                throw new IllegalArgumentException("Unknown command: " + commandName);
            }
            Command command = optCommand.get();
            if (!command.isActive()) {
                throw new IllegalArgumentException("Command is currently disabled: " + commandName);
            }

            // 3. Execute command (mock integration logic)
            Object result = executeMockCommand(commandName, intent.getAction(), intent.getParams());
            
            execution.setStatus("SUCCESS");
            execution.setResult(result);
        } catch (Exception e) {
            log.error("[Command] Execution failed: {}", e.getMessage(), e);
            execution.setStatus("FAILED");
            execution.setErrorMessage(e.getMessage());
        } finally {
            execution.setExecutionMs((int) (System.currentTimeMillis() - startTime));
            execution = executionRepo.save(execution);
        }

        return mapToResponse(execution);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAvailableCommands() {
        List<Command> commands = commandRepo.findByIsActiveTrue();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Command cmd : commands) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", cmd.getName());
            map.put("description", cmd.getDescription());
            map.put("category", cmd.getCategory());
            map.put("requiresAuth", cmd.isRequiresAuth());
            map.put("iconUrl", cmd.getIconUrl());
            result.add(map);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommandExecutionResponse> getHistory(UUID userId, Pageable pageable) {
        return executionRepo.findByUserId(userId, pageable).map(this::mapToResponse);
    }

    private Object executeMockCommand(String command, String action, Map<String, String> params) {
        return switch (command) {
            case "gmail" -> executeMockGmail(action, params);
            case "github" -> executeMockGithub(action, params);
            case "summarize" -> executeMockSummarize(params);
            case "remind" -> executeMockRemind(params);
            case "search" -> executeMockSearch(params);
            default -> throw new IllegalArgumentException("Unsupported command logic for: " + command);
        };
    }

    private Map<String, Object> executeMockGmail(String action, Map<String, String> params) {
        if ("compose".equalsIgnoreCase(action) || "send".equalsIgnoreCase(action)) {
            String to = params.getOrDefault("to", "unknown@example.com");
            String subject = params.getOrDefault("subject", "No Subject");
            return Map.of(
                "action", "send",
                "to", to,
                "subject", subject,
                "message", "Email composed and queued for sending successfully"
            );
        }
        return Map.of(
            "action", "list",
            "emails", List.of(
                Map.of("id", "m1", "from", "manager@company.com", "subject", "Weekly Sync Agenda", "snippet", "Hey team, here is the agenda..."),
                Map.of("id", "m2", "from", "billing@aws.com", "subject", "AWS Invoice Available", "snippet", "Your invoice for April is ready...")
            )
        );
    }

    private Map<String, Object> executeMockGithub(String action, Map<String, String> params) {
        String repo = params.getOrDefault("repo", "main-repo");
        if ("pr list".equalsIgnoreCase(action) || "pr".equalsIgnoreCase(action)) {
            return Map.of(
                "action", "pr list",
                "repo", repo,
                "prs", List.of(
                    Map.of("number", 101, "title", "feat: implement auth controllers", "author", "alice", "status", "open"),
                    Map.of("number", 102, "title", "fix: solve memory leak in worker", "author", "bob", "status", "merged")
                )
            );
        }
        return Map.of(
            "action", "issue list",
            "repo", repo,
            "issues", List.of(
                Map.of("number", 45, "title", "DB connection timeouts under load", "status", "open"),
                Map.of("number", 46, "title", "Frontend buttons disabled on Safari", "status", "resolved")
            )
        );
    }

    private Map<String, Object> executeMockSummarize(Map<String, String> params) {
        String text = params.getOrDefault("text", "No text provided to summarize.");
        int sentenceCount = 2;
        try {
            if (params.containsKey("sentences")) {
                sentenceCount = Integer.parseInt(params.get("sentences"));
            }
        } catch (NumberFormatException ignored) {}

        String summary = text.length() > 100 
            ? text.substring(0, Math.min(text.length(), 150)) + "... [AI Summary]"
            : text + " [AI Summary]";

        return Map.of(
            "originalLength", text.length(),
            "summary", summary,
            "sentencesTargeted", sentenceCount
        );
    }

    private Map<String, Object> executeMockRemind(Map<String, String> params) {
        String text = params.getOrDefault("task", params.getOrDefault("text", "Generic reminder"));
        String time = params.getOrDefault("time", "in 1 hour");
        return Map.of(
            "reminderId", UUID.randomUUID().toString(),
            "task", text,
            "scheduledTime", time,
            "status", "SCHEDULED"
        );
    }

    private Map<String, Object> executeMockSearch(Map<String, String> params) {
        String query = params.getOrDefault("q", params.getOrDefault("query", ""));
        return Map.of(
            "query", query,
            "results", List.of(
                Map.of("source", "gmail", "title", "Re: Weekly Sync Agenda", "url", "https://mail.google.com/mail/u/0/#inbox/123"),
                Map.of("source", "github", "title", "Issue #45: DB connection timeouts", "url", "https://github.com/workflowos/repo/issues/45")
            )
        );
    }

    private CommandExecutionResponse mapToResponse(CommandExecution execution) {
        return CommandExecutionResponse.builder()
                .id(execution.getId())
                .commandName(execution.getCommandName())
                .rawInput(execution.getRawInput())
                .status(execution.getStatus())
                .result(execution.getResult())
                .errorMessage(execution.getErrorMessage())
                .executionMs(execution.getExecutionMs())
                .executedAt(execution.getCreatedAt())
                .build();
    }
}
