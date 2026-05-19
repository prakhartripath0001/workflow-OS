# SlashAI — Antigravity AI Engineering Memory
# This is the persistent project context for the Antigravity AI assistant.
# Updated: 2026-05-20

## Project Summary
SlashAI is a production-grade AI-powered desktop workspace application.
Users control their entire digital workflow through natural language slash commands.
Example: `/gmail compose to:ceo@company.com subject:Q2 report` → AI handles it.

## Repository
- URL: https://github.com/praakhartripathi/workflow-OS
- Main branch: `main` (protected, requires PR + review)
- Integration branch: `develop` (staging)
- Commit convention: Conventional Commits (feat/fix/docs/chore/ci/security)

## Architecture Summary
```
Electron 31 (desktop shell)
  └── React 18 + Vite 5 (renderer)
        └── IPC Bridge (preload.js contextBridge — SECURITY BOUNDARY)
  └── Spring Boot 3.2.5 API (:8080)
        ├── auth/       JWT + OAuth2 (Google + GitHub)
        ├── command/    Slash command engine (Strategy pattern)
        ├── ai/         Intent recognition (regex → LLM upgrade path)
        ├── integration/ Gmail + GitHub connectors
        └── common/     ApiResponse<T>, GlobalExceptionHandler, domain exceptions
  └── PostgreSQL 16 (Flyway versioned migrations)
```

## Active Flyway Migrations
- V1: auth_users table (email, provider, role, timestamps)
- V2: commands + command_executions tables (history, JSONB result storage)
- V3: integration_tokens + user_preferences tables

## Key Patterns (Always Use These)
1. `ApiResponse<T>` — ALL REST responses wrapped in this
2. `GlobalExceptionHandler` — catches all exceptions, maps to HTTP codes
3. Strategy pattern — each slash command = `CommandHandler` @Component bean
4. IPC whitelist — new channels must be added to preload.js + ipc/ module
5. Flyway — NEVER use schema.sql for new tables; always write a V{n} migration

## Engineering Files Quick Reference
| File | Purpose |
|------|---------|
| `backend/pom.xml` | Maven deps: JJWT, Flyway, Springdoc, Testcontainers, JaCoCo |
| `backend/src/main/resources/application.properties` | Base Spring config |
| `backend/src/main/resources/application-dev.properties` | Dev overrides |
| `backend/src/main/resources/application-prod.properties` | Prod overrides |
| `frontend/src/services/apiClient.js` | Axios with JWT interceptors + refresh |
| `frontend/src/store/useAuthStore.js` | Zustand auth state |
| `frontend/electron/preload.js` | IPC security bridge |
| `.github/workflows/ci.yml` | Backend test + Frontend lint/build |
| `.github/workflows/cd.yml` | Docker build + push + deploy |
| `.github/workflows/release.yml` | Tag → GitHub Release |
| `.env.example` | All env vars documented |
| `scripts/dev-setup.sh` | One-command local setup |

## Security Constraints (Never Violate)
- `nodeIntegration: false` on every BrowserWindow — non-negotiable
- `contextIsolation: true` on every BrowserWindow — non-negotiable
- JWTs in sessionStorage only (never localStorage)
- BCrypt for all passwords (work factor ≥ 12)
- Zero secrets in source code — env vars only

## Branching Strategy
```
main        → production (protected)
develop     → staging integration
feature/*   → new features (from develop)
fix/*       → bug fixes (from develop)
hotfix/*    → urgent prod fixes (from main)
release/*   → release prep (from develop)
```

## Test Coverage Requirements
- Backend: ≥ 60% line coverage (JaCoCo)
- Frontend: ≥ 60% line coverage (Vitest)
- Backend integration tests: Testcontainers + PostgreSQL (NOT H2)

## Planned Features (v2)
- Replace regex intent parser with OpenAI function calling
- Gmail compose/read/search commands
- GitHub PR summary commands
- Command history analytics dashboard
- Electron auto-updater with signed releases
