# Generate API Endpoint — Cursor AI Prompt

## Prompt Template

```
Generate a complete REST API endpoint for SlashAI following project conventions.

Stack: Spring Boot 3.2.5, Java 17, Spring Security, Springdoc OpenAPI 2.5.0, Lombok

## Architecture Requirements
- Controller: thin, Swagger @Operation + @Tag + @SecurityRequirement(name="bearerAuth")
- Service: @Transactional where needed, @Slf4j logging, constructor injection
- DTO: @Data Lombok, @Valid Bean Validation on request DTOs
- Response: always wrapped in ApiResponse<T> from com.workflowos.common.dto
- Exception: throw domain exceptions (ResourceNotFoundException, ConflictException)

## Endpoint to Generate
HTTP Method: [GET | POST | PUT | PATCH | DELETE]
Path: /api/v1/[RESOURCE]
Auth required: [YES | NO]
Description: [WHAT THIS ENDPOINT DOES]

## Request Body (if applicable)
Fields:
- [fieldName]: [type] — [validation rules] — [description]

## Response Body
Fields:
- [fieldName]: [type] — [description]

## Business Logic
[DESCRIBE WHAT THE SERVICE LAYER SHOULD DO]

## Generate:
1. Request DTO class
2. Response DTO class
3. Service method
4. Controller method
5. Unit test for the service method
6. Example curl command
```
