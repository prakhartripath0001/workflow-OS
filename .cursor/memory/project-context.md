# SlashAI — Project Context Memory
# This file gives Cursor AI persistent context about the project architecture.
# Update this file whenever major architectural decisions are made.
# Last updated: 2026-05-20

## Project Identity
- **Name**: SlashAI
- **Type**: AI-powered desktop workspace application
- **Stage**: Early production / active development
- **Repository**: https://github.com/praakhartripathi/workflow-OS

## Tech Stack
| Layer | Technology |
|-------|-----------|
| Desktop | Electron 31 (main.js + preload.js IPC bridge) |
| Frontend | React 18 + Vite 5 + Zustand + React Router v6 |
| Styling | Tailwind CSS + CSS custom properties |
| Backend | Spring Boot 3.2.5 (Java 17) |
| Auth | Spring Security + OAuth2 Client + JJWT 0.12.5 |
| Database | PostgreSQL 16 + Flyway migrations |
| ORM | Spring Data JPA (Hibernate) |
| API Docs | Springdoc OpenAPI 2.5.0 (Swagger UI) |
| Monitoring | Spring Actuator |
| Container | Docker + Docker Compose |
| CI/CD | GitHub Actions |
| Testing (BE) | JUnit 5 + Mockito + Testcontainers |
| Testing (FE) | Vitest + React Testing Library |

## Key Architectural Decisions

### ADR-001: Modular monolith (not microservices)
Backend is a single Spring Boot app with module-based package structure.
Reason: team size is small; microservices add operational overhead without benefit at this stage.
Future: each module can be extracted to a service when traffic demands it.

### ADR-002: Flyway for DB migrations (not schema.sql)
Flyway provides versioned, repeatable, auditable migrations.
Migration files in: `backend/src/main/resources/db/migration/`
Naming: `V{n}__{description}.sql`
- V1: auth_users table
- V2: commands + command_executions tables
- V3: integration_tokens + user_preferences tables

### ADR-003: IPC whitelist pattern in Electron
Renderer process is sandboxed (`contextIsolation: true`, `nodeIntegration: false`).
Only whitelisted channel names in `preload.js` can communicate with main process.
IPC handlers are modular: `electron/ipc/authHandlers.js`, `systemHandlers.js`.

### ADR-004: Strategy pattern for slash command routing
Each command (gmail, github, summarize) implements `CommandHandler` interface.
`CommandRouter` maps command names to handlers via Spring's dependency injection.
Adding a new command = implement `CommandHandler`, annotate with `@Component`.

### ADR-005: ApiResponse<T> wrapper for all REST responses
All endpoints return `ApiResponse<T>{ success, message, data, errorCode, timestamp }`.
`GlobalExceptionHandler` maps all exceptions to correct HTTP codes + ApiResponse.

## Active Modules
- `auth/` — JWT + Google/GitHub OAuth2 login, session management
- `command/` — Slash command routing, execution, history
- `ai/` — Intent recognition (v1: regex; v2: LLM-powered)
- `integration/gmail/` — Gmail OAuth token management (planned)
- `integration/github/` — GitHub API integration (planned)
- `config/` — SecurityConfig, SwaggerConfig, CORS

## Environment Profiles
- `dev` — verbose logging, all actuator endpoints, Swagger enabled, Adminer available on :8090
- `prod` — WARN logging only, restricted actuator, Swagger disabled, Flyway enforced

## File Locations (Quick Reference)
- Backend entry point: `backend/src/main/java/com/workflowos/WorkflowOsApplication.java`
- API base URL: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Electron main: `frontend/electron/main.js`
- IPC bridge: `frontend/electron/preload.js`
- API client: `frontend/src/services/apiClient.js`
- Auth store: `frontend/src/store/useAuthStore.js`
- Command hook: `frontend/src/hooks/useCommand.js`

## Known Constraints
- Do NOT use H2 in tests — Testcontainers with real PostgreSQL only
- Do NOT store JWTs in localStorage — sessionStorage or Electron safeStorage
- Do NOT add Lombok `@Data` on JPA entities — causes infinite loop with bidirectional relations
- Do NOT disable Flyway in production — `spring.flyway.enabled` must be `true`
- Do NOT commit `.env` — only `.env.example` goes to git
