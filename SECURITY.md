# Security Policy

## Supported Versions

We actively maintain security updates for the following versions of SlashAI:

| Version | Supported          |
| ------- | ------------------ |
| 1.x     | ✅ Actively supported |
| < 1.0   | ❌ Not supported   |

---

## Reporting a Vulnerability

**⚠️ Please do NOT open a public GitHub Issue for security vulnerabilities.**

If you discover a security vulnerability in SlashAI, please report it responsibly through one of the following channels:

### Option 1: GitHub Private Security Advisory (Preferred)
Use [GitHub's private vulnerability reporting](https://github.com/praakhartripathi/workflow-OS/security/advisories/new) — this keeps the disclosure private until we issue a fix.

### Option 2: Email
Send a detailed report to: **security@slashai.app** (replace with your actual security email)

### What to include in your report:
- **Description** of the vulnerability
- **Steps to reproduce** (proof-of-concept code if available)
- **Potential impact** (what could an attacker achieve?)
- **Affected component** (Electron, Backend, Auth, Database, etc.)
- **Suggested fix** (if you have one)

---

## Our Commitment

- We will acknowledge receipt of your report within **48 hours**
- We will investigate and provide an initial assessment within **5 business days**
- We will issue a patch within **30 days** for critical vulnerabilities
- We will publicly credit you (with your permission) in the release notes

---

## Security Best Practices We Follow

### Electron Security
- `contextIsolation: true` — renderer process cannot access Node.js APIs directly
- `nodeIntegration: false` — no direct Node.js in renderer
- All IPC channels are explicitly whitelisted in `preload.js`
- `webSecurity: true` (default) — enforces same-origin policy
- External URLs always open in the system browser via `shell.openExternal()`
- No `eval()` or dynamic code execution in renderer

### Backend Security
- JWT tokens are short-lived (15 min access, 7 day refresh)
- All passwords are hashed with BCrypt (work factor 12)
- OAuth2 PKCE flow for Google and GitHub
- CORS restricted to known origins
- SQL injection prevented via JPA parameterized queries
- Secrets loaded from environment variables only (never hardcoded)
- OWASP dependency check runs on every CI build
- Spring Security CSRF protection enabled

### Data Security
- Database credentials never stored in version control
- `.env` files listed in `.gitignore`
- Database connection uses TLS in production
- Sensitive columns encrypted at rest (future roadmap)

### Infrastructure Security
- Docker containers run as non-root users
- Minimal base images (Alpine) to reduce attack surface
- Secrets managed via environment variables or a secrets manager (Vault/AWS SSM)
- Network isolation via Docker bridge networks

---

## Known Security Limitations

- The Electron app does not yet enforce certificate pinning
- Session tokens are stored in memory (not persisted to disk)

---

## Disclosure Policy

We follow a coordinated disclosure policy. Once a patch is released, we will publish a security advisory with full details of the vulnerability and credit the reporter.
