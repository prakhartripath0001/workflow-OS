package com.workflowos.ai.model;

import lombok.*;

import java.util.Collections;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParsedIntent {
    private String command;
    private String action;
    private Map<String, String> params;
    private String rawInput;
    private double confidence;

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
