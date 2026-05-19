# Architecture Rules — SlashAI Codex

## Modular Monolith Structure
SlashAI uses a **modular monolith** — one deployable Spring Boot app, but internally organized as independent modules.

### Module Map
```
com.workflowos/
  auth/           Authentication, JWT, OAuth2
  command/        Slash command engine
  ai/             Intent recognition
  integration/
    gmail/        Gmail API
    github/       GitHub API
  config/         Security, Swagger, CORS, Web config
  common/         Shared: exceptions, DTOs, utilities
```

### Module Dependency Rules (enforced by code review)
```
controller → service → repository → entity
                     ↘ ai/ (for intent parsing)
                     ↘ integration/ (for external calls)
```
- Modules communicate via Spring service injection — never via HTTP
- `common/` is shared by all — it must have zero dependencies on other modules
- `config/` imports from Spring only — no business module imports

## Clean Architecture Principles
1. **Dependency inversion**: Controller depends on service interface, not implementation
2. **Single responsibility**: One class = one reason to change
3. **No God classes**: Services > 500 lines must be split
4. **Immutable DTOs**: Use `@Builder` + final fields for response DTOs

## API Design Standards
- Base path: `/api/v1/`
- Plural resource names: `/api/v1/commands` (not `/command`)
- HTTP verbs: GET (read), POST (create), PUT (replace), PATCH (update), DELETE
- Status codes: 200 (ok), 201 (created), 400 (validation), 401 (auth), 403 (forbidden), 404 (not found), 409 (conflict)
- All responses: `ApiResponse<T>` wrapper
- Pagination: `?page=0&size=20` via `Pageable`

## Database Architecture
- All tables in `public` schema
- `id BIGSERIAL PRIMARY KEY` on every table
- `created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()` on every table
- `updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()` with trigger
- Flyway manages all schema changes — no DDL outside of migration files

## Future Scaling Points
When traffic grows, these modules can be extracted to microservices:
1. `ai/` → AI Inference Service (GPU-optimized)
2. `integration/gmail/` → Gmail Connector Service
3. `integration/github/` → GitHub Connector Service
Each module has clean interfaces today for this reason.
