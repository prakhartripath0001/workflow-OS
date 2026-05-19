# Fix Bug — Cursor AI Prompt

## Prompt Template

```
I have a bug in SlashAI. Help me diagnose and fix it.

Project: Electron + React 18 + Spring Boot 3.2.5 + PostgreSQL 16
Architecture: modular monolith, thin controllers, service layer, ApiResponse<T> wrapper

## Bug Description
[DESCRIBE THE BUG — what happens vs what should happen]

## Error Message / Stack Trace
```
[PASTE ERROR HERE]
```

## Affected Component
- [ ] Electron main process
- [ ] IPC bridge (preload.js)
- [ ] React component
- [ ] Zustand store
- [ ] Axios service (apiClient.js)
- [ ] Spring Boot controller
- [ ] Spring Boot service
- [ ] Spring Security / Auth
- [ ] Flyway migration
- [ ] PostgreSQL / JPA
- [ ] Docker / networking

## Steps to Reproduce
1. 
2. 
3. 

## Relevant Files
[LIST OR PASTE RELEVANT FILES]

## Constraints
- Do not change the ApiResponse<T> wrapper shape
- Do not remove contextIsolation or nodeIntegration restrictions in Electron
- Do not use H2 in tests — Testcontainers with PostgreSQL only
- Follow existing code patterns in the affected module
```
