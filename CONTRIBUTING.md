# Contributing to SlashAI

> Thank you for your interest in contributing to **SlashAI** — an AI-powered desktop workspace built with Electron, React, and Spring Boot. Every contribution, from a typo fix to a major feature, is valued.

---

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Branching Strategy](#branching-strategy)
- [Commit Convention](#commit-convention)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Testing Requirements](#testing-requirements)
- [Documentation](#documentation)

---

## Code of Conduct

By participating in this project, you agree to uphold a respectful, inclusive, and constructive environment. Be kind, assume good intent, and focus on ideas rather than people.

---

## Getting Started

1. **Fork** this repository to your own GitHub account
2. **Clone** your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/workflow-OS.git
   cd workflow-OS
   ```
3. **Add the upstream remote:**
   ```bash
   git remote add upstream https://github.com/praakhartripathi/workflow-OS.git
   ```
4. **Copy and configure environment variables:**
   ```bash
   cp .env.example .env
   # Edit .env with your local credentials
   ```

---

## Development Setup

### Prerequisites

| Tool | Minimum Version | Install |
|------|----------------|---------|
| Node.js | 20.x LTS | [nodejs.org](https://nodejs.org) |
| Java (Temurin) | 17 | [adoptium.net](https://adoptium.net) |
| Maven | 3.9+ | `brew install maven` |
| Docker Desktop | 4.x | [docker.com](https://docker.com) |
| PostgreSQL | 16 (via Docker) | Included in `docker-compose.yml` |

### Start the Full Stack

```bash
# Start all services via Docker Compose
docker compose up -d

# OR start services individually for development:

# 1. Start PostgreSQL only
docker compose up -d postgres

# 2. Start backend (Spring Boot)
cd backend && mvn spring-boot:run

# 3. Start frontend (Vite + Electron)
cd frontend && npm install && npm run electron:dev
```

---

## Branching Strategy

We follow **GitHub Flow** with long-lived environment branches:

```
main           ← production-ready code (protected, requires PR + 1 review)
develop        ← integration branch (staging)
feature/*      ← new features  (branch from develop)
fix/*          ← bug fixes     (branch from develop)
hotfix/*       ← urgent prod fixes (branch from main)
chore/*        ← maintenance tasks, deps, CI
docs/*         ← documentation-only changes
release/x.y.z  ← release preparation branch
```

**Never commit directly to `main` or `develop`.**

```bash
# Example: starting a new feature
git checkout develop
git pull upstream develop
git checkout -b feature/slash-command-gmail-compose
```

---

## Commit Convention

We use [Conventional Commits](https://www.conventionalcommits.org/) spec:

```
<type>(<scope>): <short summary>

[optional body]

[optional footer(s)]
```

### Types

| Type | When to use |
|------|-------------|
| `feat` | A new feature |
| `fix` | A bug fix |
| `docs` | Documentation only changes |
| `style` | Formatting, no logic change |
| `refactor` | Code refactor, no new feature or fix |
| `perf` | Performance improvement |
| `test` | Adding or fixing tests |
| `chore` | Build process, tooling, deps |
| `ci` | CI/CD changes |
| `security` | Security fixes or hardening |

### Scopes (examples)

`auth`, `ai`, `command`, `gmail`, `github`, `electron`, `ipc`, `db`, `api`, `ui`, `docker`, `docs`

### Examples

```bash
git commit -m "feat(command): add /gmail compose slash command"
git commit -m "fix(auth): resolve JWT expiry refresh race condition"
git commit -m "chore(deps): bump spring-boot from 3.2.5 to 3.3.0"
git commit -m "docs(readme): add Flyway migration setup instructions"
git commit -m "security(oauth): enforce PKCE for Google OAuth2 flow"

# Breaking change — add ! after type/scope:
git commit -m "feat(api)!: rename /api/v1/user to /api/v1/users"
```

---

## Pull Request Process

1. **Keep PRs focused** — one feature or fix per PR
2. **Write descriptive PR titles** following the commit convention
3. **Fill in the PR template** completely
4. **Self-review your diff** before requesting a review
5. **All CI checks must pass** before merging
6. **Require 1 approving review** for `develop`, 2 for `main`
7. **Squash and merge** to keep history clean
8. **Update `CHANGELOG.md`** under `[Unreleased]` section

---

## Coding Standards

### Java / Spring Boot

- Follow the **Google Java Style Guide**
- Use `@Slf4j` (Lombok) for logging — no `System.out.println`
- Use constructor injection — avoid `@Autowired` on fields
- All public service methods must have JavaDoc
- Keep controllers thin — business logic lives in services
- Use DTOs for API request/response — never expose JPA entities directly
- Package structure: `controller → service → repository → entity`

### React / JavaScript

- Use **functional components** with hooks — no class components
- **Named exports** preferred over default exports for components
- Keep components small and single-responsibility
- API calls belong in `src/services/` — not inside components
- Custom hooks for reusable stateful logic in `src/hooks/`
- State management in `src/store/` using Zustand

### CSS / Styling

- Use CSS custom properties (variables) defined in `index.css`
- Avoid magic numbers — use the design token variables
- Mobile-first responsive breakpoints

---

## Testing Requirements

### Backend

- **Unit tests** for all service layer methods (`src/test/java`)
- **Integration tests** for repositories and REST endpoints
- Minimum **60% line coverage** (enforced in CI)
- Use `@SpringBootTest` with an embedded H2 or Testcontainers for integration tests

```bash
cd backend
mvn test                    # run all tests
mvn test -Dtest=AuthService # run specific test
mvn jacoco:report           # generate coverage report
```

### Frontend

- Unit tests with **Vitest** for utility functions and custom hooks
- Component tests with **React Testing Library**

```bash
cd frontend
npm test                    # run all tests
npm run test:coverage       # with coverage report
```

---

## Documentation

- Update inline comments when changing complex logic
- Update `README.md` if you change setup steps or architecture
- Update `docs/` for architectural decisions (ADRs)
- Add JSDoc to exported functions in `src/utils/` and `src/services/`
- Add JavaDoc to all public service and controller methods

---

## Questions?

Open a [GitHub Discussion](https://github.com/praakhartripathi/workflow-OS/discussions) or reach out via the issue tracker. We're happy to help!
