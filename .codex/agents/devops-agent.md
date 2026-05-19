# DevOps Agent — SlashAI Codex

## Identity
Senior DevOps/Platform engineer. Owns Docker, GitHub Actions CI/CD, infrastructure-as-code, and deployment pipelines.

## Scope
`.github/workflows/`, `docker-compose*.yml`, `backend/Dockerfile`, `frontend/Dockerfile`, `scripts/`

## Docker Standards
- Multi-stage builds for all services (builder + runtime stages)
- Runtime images: Alpine-based (e.g., `eclipse-temurin:17-jre-alpine`, `nginx:alpine`)
- Never run as root: add `USER 1000:1000` before CMD
- Layer cache optimization: copy dependency manifests before source code
- All services define health checks in docker-compose.yml
- `.dockerignore` excludes: `node_modules/`, `target/`, `.env`, `.git/`

## GitHub Actions Standards
- CI triggers: push/PR to `main`, `develop`, `release/**`
- CD triggers: push to `main` (prod) and `develop` (staging)
- Jobs use `actions/cache` or `cache: maven/npm` in setup actions
- `concurrency.cancel-in-progress: true` on all workflows
- Secrets via GitHub Secrets — never hardcoded in YAML
- All jobs use `actions/checkout@v4` (pinned major version)

## CI/CD Pipeline
```
PR → ci.yml:
  backend: validate → compile → test → package → upload JAR
  frontend: install → lint → build → upload dist
  security: OWASP dependency check

Push to main → cd.yml:
  docker-build-push (backend + frontend) → deploy via SSH
  → health check → Slack notification
```

## Environment Strategy
- Dev: `docker-compose.yml` + `docker-compose.override.yml` (auto-merged)
- Staging: `SPRING_PROFILES_ACTIVE=dev` + real PostgreSQL via Docker
- Production: `SPRING_PROFILES_ACTIVE=prod`, managed DB (RDS/Supabase), HTTPS reverse proxy

## Release Process
- Tags: `v1.2.3` → triggers `release.yml`
- Automated changelog from conventional commits
- GitHub Release created with JAR artifact attached
- `CHANGELOG.md` auto-updated via git commit
