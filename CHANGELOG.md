# Changelog

All notable changes to **SlashAI** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Added
- Engineering infrastructure: CI/CD pipelines, issue templates, PR templates
- `CONTRIBUTING.md`, `SECURITY.md`, `CODEOWNERS` added
- Flyway database migration strategy setup
- ESLint + Prettier configuration
- Swagger / OpenAPI documentation endpoint

### Changed
- N/A

### Fixed
- N/A

### Security
- N/A

---

## [1.0.0] — 2026-05-20

### Added
- ✨ Initial release of SlashAI desktop workspace
- Electron + React frontend with Vite bundler
- Spring Boot 3.2.5 backend with PostgreSQL 16
- Google OAuth2 and GitHub OAuth2 authentication
- JWT-based session management
- `/` slash command input bar
- Docker Compose full-stack setup with health checks
- `application.properties` with profile-aware configuration
- `schema.sql` and `data.sql` initialization with `^^` separator for PL/pgSQL support
- HikariCP connection pool configuration
- Spring Actuator health/info endpoints
- Electron window with `contextIsolation: true` and `nodeIntegration: false`
- macOS native title bar (`hiddenInset`) and dock support

### Security
- BCrypt password hashing (work factor 12)
- OAuth2 with scope-limited token exchange
- Electron preload-based IPC bridge (no direct Node.js in renderer)

---

## [0.2.0] — 2026-05-13

### Fixed
- Resolved `ScriptStatementFailedException` for PostgreSQL dollar-quoted SQL (`DO $$ ... $$`) blocks by switching SQL separator to `^^`

---

## [0.1.0] — 2026-05-03

### Added
- Monorepo scaffold: `frontend/` + `backend/` + root `docker-compose.yml`
- Spring Boot application bootstrap
- React + Vite project initialization
- Electron main process setup
- PostgreSQL schema and seed data files
- `.env.example` environment configuration template
- `.gitignore` for Java, Node.js, and Electron artifacts

---

[Unreleased]: https://github.com/praakhartripathi/workflow-OS/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/praakhartripathi/workflow-OS/compare/v0.2.0...v1.0.0
[0.2.0]: https://github.com/praakhartripathi/workflow-OS/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/praakhartripathi/workflow-OS/releases/tag/v0.1.0
