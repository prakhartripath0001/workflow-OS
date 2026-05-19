# Generate API Command — SlashAI Codex

## Command: `generate-api`
## Purpose: Generate a complete, production-ready REST API endpoint

## Usage
Provide the spec below and ask the AI to generate the full implementation.

---

## Input Spec

```yaml
endpoint:
  method: POST           # GET | POST | PUT | PATCH | DELETE
  path: /api/v1/commands/execute
  auth: required         # required | public
  description: Execute a slash command

request:
  fields:
    - name: rawInput
      type: String
      validation: "@NotBlank, @Size(max=2000)"
      description: Full user input string starting with /

response:
  fields:
    - name: id
      type: Long
    - name: commandName
      type: String
    - name: status
      type: String
    - name: result
      type: Object

service_logic: |
  1. Parse intent using IntentRecognitionService
  2. Route to CommandHandler strategy
  3. Save execution to command_executions table
  4. Return CommandExecutionResponse

generate:
  - RequestDTO       # with validation annotations
  - ResponseDTO      # with @Builder
  - ServiceMethod    # @Transactional where needed
  - Controller       # thin, with @Operation Swagger annotation
  - UnitTest         # happy path + error path
  - CurlExample      # ready to copy-paste
```

## Output Requirements
- Response must use `ApiResponse<T>` wrapper
- Exceptions: domain exceptions only (`ResourceNotFoundException`, etc.)
- Controller method must have `@Operation(summary=...)` + `@SecurityRequirement(name="bearerAuth")`
- Test: `@ExtendWith(MockitoExtension.class)`, mock all dependencies
- No `System.out.println` — use `@Slf4j`
