# Engineering Standards — Antigravity AI

## Commit Convention (Conventional Commits)
```
<type>(<scope>): <description>

Types: feat | fix | docs | style | refactor | perf | test | chore | ci | security
Scopes: auth | ai | command | gmail | github | electron | ipc | db | api | ui | docker | docs
```

Examples:
- `feat(command): add /github pr-list command handler`
- `fix(auth): resolve JWT refresh race condition on concurrent requests`
- `security(electron): enforce CSP in index.html`
- `chore(deps): bump JJWT from 0.12.3 to 0.12.5`

## Branching
- `feature/*` — new features (branch from develop)
- `fix/*` — bug fixes (branch from develop)
- `hotfix/*` — urgent prod fix (branch from main, merge to both main + develop)
- `release/x.y.z` — release prep

## Code Review Standards
- PRs require at minimum 1 approval before merge
- All CI checks must pass
- Squash and merge to keep history linear
- PR title follows commit convention

## Java Style
- 4-space indentation
- Max line length: 120 chars
- Google Java Style Guide
- Lombok allowed: `@Slf4j`, `@Data`, `@Builder`, `@RequiredArgsConstructor`
- No `@Data` on JPA entities (circular reference risk)

## JavaScript / JSX Style
- Prettier: single quotes, no semicolons, trailing commas, 100 char width
- ESLint: extends eslint:recommended + react + react-hooks + import

## Naming
| Context | Convention | Example |
|---------|-----------|---------|
| Java class | PascalCase | `CommandService` |
| Java method | camelCase | `executeCommand` |
| Java constant | UPPER_SNAKE | `MAX_RETRY_COUNT` |
| React component | PascalCase | `CommandBar` |
| React hook | camelCase + use | `useCommand` |
| JS service | camelCase + Service | `authService` |
| SQL table | snake_case | `command_executions` |
| SQL column | snake_case | `created_at` |
| Env variable | UPPER_SNAKE | `JWT_SECRET` |
| URL path | kebab-case | `/api/v1/slash-commands` |
