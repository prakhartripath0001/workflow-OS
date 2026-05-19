# Review PR — Cursor AI Prompt

## Usage
Use this before merging any PR to get an AI-assisted review.

---

## Prompt Template

```
Review this PR diff for the SlashAI project. Apply these review criteria:

## Security Checklist
- [ ] No secrets, API keys, or passwords in code or config
- [ ] JWT handling follows project standards (sessionStorage, not localStorage)
- [ ] Electron: no new unsafe IPC channels outside the whitelist
- [ ] New endpoints: protected with Spring Security (@PreAuthorize)
- [ ] SQL queries: JPA parameterized only — no string concatenation

## Architecture Checklist
- [ ] Backend: controllers are thin, business logic is in service layer
- [ ] All REST responses use ApiResponse<T> wrapper
- [ ] Domain exceptions used (ResourceNotFoundException, ConflictException, ValidationException)
- [ ] New DB tables have a Flyway migration file
- [ ] Frontend: API calls are in src/services/, not in component body
- [ ] New IPC channels: added to preload.js whitelist AND ipc/ handler

## Code Quality Checklist
- [ ] No console.log in production code
- [ ] No unused variables or imports
- [ ] Meaningful variable and method names
- [ ] Methods under 30 lines (otherwise suggest extraction)
- [ ] DTOs used for all API request/response (no JPA entity exposure)

## Testing Checklist
- [ ] New service methods have unit tests
- [ ] Happy path + error path covered
- [ ] No H2 — Testcontainers used for integration tests
- [ ] Frontend: new hooks/utilities have Vitest tests

## Diff to Review:
[PASTE DIFF HERE]
```
