# Create Feature — Cursor AI Prompt

## Usage
Use this prompt when adding a new feature to SlashAI.
Copy and paste into Cursor Chat, filling in the `[FEATURE]` placeholder.

---

## Prompt Template

```
I'm adding a new feature to SlashAI: [FEATURE DESCRIPTION]

Tech stack context:
- Backend: Spring Boot 3.2.5, Java 17, PostgreSQL 16, Flyway, JJWT
- Frontend: React 18, Vite 5, Zustand, Axios (apiClient.js)
- Desktop: Electron 31 with contextBridge IPC

Architecture rules to follow:
1. Backend: thin controller → service → repository → entity pattern
2. All endpoints return ApiResponse<T> wrapper
3. Use GlobalExceptionHandler — throw domain exceptions, never catch in controllers
4. New DB tables = new Flyway migration file (V{next}__description.sql)
5. Frontend API calls go through src/services/ — never in component body
6. New IPC channels must be added to preload.js whitelist AND ipc/ handler

Please generate:
1. Flyway migration SQL (if DB changes needed)
2. JPA Entity class
3. Spring Data Repository interface
4. Service class with business logic
5. Controller with Swagger @Operation annotations
6. Request/Response DTOs
7. Frontend service function in src/services/
8. React hook in src/hooks/ if needed
9. Unit test skeleton for the service class

Feature: [DESCRIBE YOUR FEATURE HERE]
```
