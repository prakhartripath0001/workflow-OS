// =============================================================================
// AI Intent Recognition Service
// Routes natural language slash command input to the correct handler.
// Architecture: Strategy Pattern — each command name maps to a handler bean.
// =============================================================================
package com.workflowos.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses raw slash command input and extracts structured intent.
 *
 * Examples:
 *   "/gmail compose to:foo@bar.com subject:Hello body:World"
 *   → Intent{ command="gmail", action="compose", params={to, subject, body} }
 *
 *   "/github pr list repo:myorg/myrepo state:open"
 *   → Intent{ command="github", action="pr list", params={repo, state} }
 */
@Slf4j
@Service
public class IntentRecognitionService {

    // Matches /commandName optionalAction key:value pairs
    private static final Pattern COMMAND_PATTERN =
            Pattern.compile("^/([a-z][a-z0-9-]*)(?:\\s+([a-z][a-z0-9\\s]*))?(.*)$",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern PARAM_PATTERN =
            Pattern.compile("(\\w+):([^\\s]+|\"[^\"]*\")");

    /**
     * Parses raw input into a structured {@link ParsedIntent}.
     *
     * @param rawInput The full user input string (must start with '/')
     * @return Parsed intent or a fallback UNKNOWN intent
     */
    public ParsedIntent parse(String rawInput) {
        if (rawInput == null || rawInput.isBlank() || !rawInput.startsWith("/")) {
            log.warn("[AI] Invalid command input — must start with '/': '{}'", rawInput);
            return ParsedIntent.unknown(rawInput);
        }

        Matcher matcher = COMMAND_PATTERN.matcher(rawInput.trim());
        if (!matcher.matches()) {
            log.warn("[AI] Could not parse command: '{}'", rawInput);
            return ParsedIntent.unknown(rawInput);
        }

        String commandName = matcher.group(1).toLowerCase();
        String action      = matcher.group(2) != null ? matcher.group(2).trim() : "";
        String paramString = matcher.group(3) != null ? matcher.group(3).trim() : "";

        Map<String, String> params = extractParams(paramString);

        log.debug("[AI] Parsed intent — command='{}' action='{}' params={}", commandName, action, params);

        return ParsedIntent.builder()
                .command(commandName)
                .action(action)
                .params(params)
                .rawInput(rawInput)
                .confidence(calculateConfidence(commandName, action))
                .build();
    }

    private Map<String, String> extractParams(String paramString) {
        java.util.LinkedHashMap<String, String> params = new java.util.LinkedHashMap<>();
        Matcher m = PARAM_PATTERN.matcher(paramString);
        while (m.find()) {
            String value = m.group(2).replaceAll("^\"|\"$", ""); // strip quotes
            params.put(m.group(1).toLowerCase(), value);
        }
        return params;
    }

    /**
     * Simple rule-based confidence score.
     * In v2: replace with an LLM call (OpenAI / local Ollama) for fuzzy matching.
     */
    private double calculateConfidence(String command, String action) {
        // Known commands get high confidence; unknown get low
        return switch (command) {
            case "gmail", "github", "summarize", "remind", "search" -> 0.95;
            default -> 0.40;
        };
    }
}
