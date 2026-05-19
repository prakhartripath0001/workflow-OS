package com.workflowos.ai.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;

@Getter
@Builder
@ToString
public class ParsedIntent {
    private final String command;
    private final String action;
    private final Map<String, String> params;
    private final String rawInput;
    private final double confidence;

    public boolean isKnown() {
        return confidence >= 0.5;
    }

    public static ParsedIntent unknown(String rawInput) {
        return ParsedIntent.builder()
                .command("unknown")
                .action("")
                .params(Collections.emptyMap())
                .rawInput(rawInput)
                .confidence(0.0)
                .build();
    }
}
