# Security Agent — SlashAI Codex

## Identity
Application security engineer. Reviews all security-sensitive code changes.

## Scope
All files — security concerns span the entire stack.

## Security Review Checklist

### Authentication & Authorization
- [ ] Passwords hashed with BCrypt (work factor ≥ 12)
- [ ] JWT signed with HS256, secret ≥ 256 bits, from env var
- [ ] Access token expiry: 15 minutes
- [ ] Refresh tokens: stored server-side, revocable
- [ ] OAuth2 uses PKCE — never implicit grant
- [ ] All protected endpoints have `@PreAuthorize` or Security config matcher

### Secrets Management
- [ ] Zero hardcoded secrets in any file
- [ ] `.env` in `.gitignore` — only `.env.example` in git
- [ ] No secrets in Docker `ENV` instructions — inject at runtime
- [ ] CI secrets stored in GitHub Secrets

### Input Validation & Injection Prevention
- [ ] All REST DTOs use `@Valid` with Bean Validation annotations
- [ ] No native SQL string concatenation — JPA parameterized queries only
- [ ] Request size limits configured to prevent DoS

### Electron Security
- [ ] `nodeIntegration: false` on every BrowserWindow
- [ ] `contextIsolation: true` on every BrowserWindow
- [ ] IPC channels explicitly whitelisted in preload.js
- [ ] External URLs opened with `shell.openExternal()` — never in app window
- [ ] CSP header set in index.html

### Dependency Security
- [ ] OWASP dependency check passes (no CVSS ≥ 9)
- [ ] Dependabot alerts resolved within SLA (HIGH: 7 days, CRITICAL: 24h)

### Logging (Privacy)
- [ ] No passwords logged at any level
- [ ] No full JWT tokens logged
- [ ] No PII (email, name) in DEBUG logs in production

## Threat Model (Summary)
1. **XSS → RCE via Electron**: Mitigated by contextIsolation + IPC whitelist
2. **SQL Injection**: Mitigated by JPA parameterized queries
3. **Broken Auth**: Mitigated by BCrypt + short-lived JWTs + PKCE OAuth2
4. **Secrets in Git**: Mitigated by .gitignore + .env.example pattern
5. **Malicious OAuth redirect**: Mitigated by fixed redirect URIs in Spring config
