<div align="center">

<img src="docs/assets/logo.png" alt="SlashAI Logo" width="120" height="120" />

# SlashAI — AI Desktop Workspace

**A production-ready, AI-powered desktop workspace built with Electron, React & Spring Boot**

[![CI](https://github.com/praakhartripathi/workflow-OS/actions/workflows/ci.yml/badge.svg)](https://github.com/praakhartripathi/workflow-OS/actions/workflows/ci.yml)
[![CD](https://github.com/praakhartripathi/workflow-OS/actions/workflows/cd.yml/badge.svg)](https://github.com/praakhartripathi/workflow-OS/actions/workflows/cd.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk)](https://adoptium.net)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?logo=spring)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-61DAFB?logo=react)](https://react.dev)
[![Electron](https://img.shields.io/badge/Electron-31-47848F?logo=electron)](https://electronjs.org)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql)](https://postgresql.org)

[**Live Demo**](#) · [**Documentation**](docs/) · [**Report Bug**](.github/ISSUE_TEMPLATE/bug_report.yml) · [**Request Feature**](.github/ISSUE_TEMPLATE/feature_request.yml)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Quick Start](#-quick-start)
- [Development Setup](#-development-setup)
- [Environment Variables](#-environment-variables)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Contributing](#-contributing)
- [Security](#-security)
- [License](#-license)

---

## 🔍 Overview

**SlashAI** is an AI-powered desktop productivity workspace that lets you control your entire digital workflow through natural language slash commands. Type `/gmail compose`, `/github pr review`, or `/summarize` and watch SlashAI handle it autonomously.

Built as a native desktop application using **Electron** with a **React** frontend, backed by a **Spring Boot** microservice API, secured with **JWT + OAuth2**, and powered by an AI intent recognition engine.

---

## ✨ Features

| Feature | Status |
|---------|--------|
| 🔐 Google & GitHub OAuth2 login | ✅ |
| ⌨️ Slash command interface | ✅ |
| 🤖 AI-powered intent recognition | 🚧 In Progress |
| 📧 Gmail integration | 🚧 In Progress |
| 🐙 GitHub integration | 🚧 In Progress |
| 📊 Workflow automation | 🗓️ Planned |
| 🔔 Smart notifications | 🗓️ Planned |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Electron Shell                        │
│  ┌───────────────────────────────────────────────────┐  │
│  │              React Renderer (Vite)                 │  │
│  │  ┌──────────┐  ┌──────────┐  ┌────────────────┐   │  │
│  │  │  Pages   │  │Components│  │  Zustand Store │   │  │
│  │  └────┬─────┘  └────┬─────┘  └────────────────┘   │  │
│  │       └─────────────┘                              │  │
│  │              IPC Bridge (preload.js)               │  │
│  └───────────────────────┬───────────────────────────┘  │
│                           │ contextBridge                │
│  ┌────────────────────────▼──────────────────────────┐  │
│  │              Electron Main Process                 │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────┬───────────────────────────────┘
                          │ HTTP / REST
                          ▼
┌─────────────────────────────────────────────────────────┐
│               Spring Boot API (Port 8080)                │
│  ┌──────────┐  ┌──────────┐  ┌───────────┐  ┌───────┐  │
│  │Auth Layer│  │ Commands │  │AI Service │  │Integr.│  │
│  │JWT+OAuth2│  │ Handler  │  │  Layer    │  │Gmail  │  │
│  └────┬─────┘  └────┬─────┘  └─────┬─────┘  └───────┘  │
│       └─────────────┼───────────────┘                   │
│              ┌──────▼──────┐                            │
│              │  PostgreSQL  │ (Flyway migrations)        │
│              └─────────────┘                            │
└─────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| Desktop Shell | Electron 31 | Native OS integration, IPC, system tray |
| Frontend | React 18 + Vite 5 | UI components, routing, state |
| State | Zustand | Lightweight global state management |
| Styling | Tailwind CSS + CSS Variables | Design system |
| Backend | Spring Boot 3.2.5 (Java 17) | REST API, business logic |
| Auth | Spring Security + OAuth2 + JWT | Authentication & authorization |
| Database | PostgreSQL 16 + Flyway | Persistent storage + migrations |
| ORM | Spring Data JPA (Hibernate) | Database access layer |
| API Docs | Springdoc OpenAPI (Swagger) | Interactive API documentation |
| Monitoring | Spring Actuator | Health checks, metrics |
| Container | Docker + Docker Compose | Local dev & production deployment |
| CI/CD | GitHub Actions | Automated testing and deployment |

---

## 📁 Project Structure

```
workflow-OS/
├── .github/
│   ├── workflows/
│   │   ├── ci.yml              # CI — build, test, lint
│   │   ├── cd.yml              # CD — Docker build & deploy
│   │   └── release.yml         # Release automation
│   ├── ISSUE_TEMPLATE/
│   │   ├── bug_report.yml
│   │   └── feature_request.yml
│   ├── PULL_REQUEST_TEMPLATE/
│   │   └── pull_request_template.md
│   └── CODEOWNERS
│
├── backend/                    # Spring Boot application
│   ├── src/main/java/com/workflowos/
│   │   ├── WorkflowOsApplication.java
│   │   ├── auth/               # JWT + OAuth2 authentication module
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── handler/
│   │   ├── command/            # Slash command processing
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   └── model/
│   │   ├── ai/                 # AI intent recognition service
│   │   │   ├── service/
│   │   │   └── model/
│   │   ├── integration/        # External API integrations
│   │   │   ├── gmail/
│   │   │   └── github/
│   │   ├── config/             # Spring Security, Swagger, CORS
│   │   └── common/             # Shared utilities, exceptions, DTOs
│   ├── src/main/resources/
│   │   ├── db/
│   │   │   ├── migration/      # Flyway versioned migrations
│   │   │   │   ├── V1__create_users_table.sql
│   │   │   │   ├── V2__create_commands_table.sql
│   │   │   │   └── V3__add_oauth_providers.sql
│   │   │   └── (legacy schema.sql — will be replaced by Flyway)
│   │   ├── application.properties
│   │   ├── application-dev.properties
│   │   └── application-prod.properties
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                   # Electron + React application
│   ├── electron/
│   │   ├── main.js             # Electron main process
│   │   ├── preload.js          # Secure IPC bridge
│   │   └── ipc/                # IPC channel handlers
│   │       ├── authHandlers.js
│   │       └── systemHandlers.js
│   ├── src/
│   │   ├── pages/              # Route-level page components
│   │   ├── components/         # Reusable UI components
│   │   │   ├── CommandBar/     # Slash command input
│   │   │   ├── layout/         # App shell, sidebar, header
│   │   │   └── ui/             # Primitives: Button, Modal, etc.
│   │   ├── hooks/              # Custom React hooks
│   │   ├── store/              # Zustand state stores
│   │   ├── services/           # API client functions (Axios)
│   │   ├── utils/              # Pure utility functions
│   │   └── types/              # JSDoc type definitions / TS declarations
│   ├── index.html
│   ├── vite.config.js
│   ├── package.json
│   └── Dockerfile
│
├── docs/                       # Project documentation
│   ├── architecture/           # Architecture Decision Records (ADRs)
│   ├── api/                    # API docs (auto-generated from OpenAPI)
│   └── guides/                 # Developer guides
│
├── scripts/                    # Helper shell scripts
│   ├── dev-setup.sh            # One-command dev environment setup
│   ├── db-reset.sh             # Drop & recreate database
│   └── generate-jwt-secret.sh  # Generate cryptographically secure JWT secret
│
├── docker-compose.yml
├── docker-compose.override.yml # Dev overrides (volume mounts, hot reload)
├── .env.example
├── .gitignore
├── CHANGELOG.md
├── CONTRIBUTING.md
├── SECURITY.md
└── README.md
```

---

## 🚀 Quick Start

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop) 4.x+
- [Node.js](https://nodejs.org) 20.x LTS
- [Java 17](https://adoptium.net) (Temurin)

### 1. Clone

```bash
git clone https://github.com/praakhartripathi/workflow-OS.git
cd workflow-OS
```

### 2. Configure Environment

```bash
cp .env.example .env
# Edit .env with your OAuth credentials
```

### 3. Start All Services

```bash
docker compose up -d
```

| Service | URL |
|---------|-----|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080 |
| API Docs (Swagger) | http://localhost:8080/swagger-ui.html |
| Health Check | http://localhost:8080/actuator/health |

### 4. Run as Desktop App

```bash
cd frontend
npm install
npm run electron:dev
```

---

## 💻 Development Setup

See [CONTRIBUTING.md](CONTRIBUTING.md) for full development setup instructions.

### Backend Only

```bash
cd backend
# Start PostgreSQL
docker compose up -d postgres

# Run Spring Boot with dev profile
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

### Frontend Only

```bash
cd frontend
npm install
npm run dev          # Web browser (port 5173)
npm run electron:dev # Electron desktop app
```

---

## 🔐 Environment Variables

See [`.env.example`](.env.example) for a complete list of required variables.

| Variable | Description | Required |
|----------|-------------|----------|
| `POSTGRES_DB` | Database name | ✅ |
| `POSTGRES_USER` | Database user | ✅ |
| `POSTGRES_PASSWORD` | Database password | ✅ |
| `GOOGLE_CLIENT_ID` | Google OAuth2 client ID | ✅ |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret | ✅ |
| `GITHUB_CLIENT_ID` | GitHub OAuth2 client ID | ✅ |
| `GITHUB_CLIENT_SECRET` | GitHub OAuth2 client secret | ✅ |
| `JWT_SECRET` | JWT signing secret (≥256 bits) | ✅ |
| `JWT_EXPIRY_MS` | Access token expiry (ms) | ✅ |

---

## 📖 API Documentation

Once the backend is running, interactive API documentation is available at:

```
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs    # OpenAPI JSON spec
```

---

## 🧪 Testing

### Backend Tests

```bash
cd backend
mvn test                          # Run all unit tests
mvn verify                        # Run integration tests too
mvn jacoco:report                 # Generate coverage report (target/site/jacoco/)
```

### Frontend Tests

```bash
cd frontend
npm test                          # Run Vitest unit tests
npm run test:coverage             # With coverage report
```

---

## 🚢 Deployment

### Docker Compose (Production)

```bash
# Build and start in production mode
docker compose -f docker-compose.yml up -d --build

# View logs
docker compose logs -f backend
```

### Environment Recommendations for Production

- Use a managed PostgreSQL instance (AWS RDS, Supabase, Neon)
- Store secrets in AWS SSM Parameter Store or HashiCorp Vault
- Use a reverse proxy (nginx, Caddy) with TLS termination
- Enable `SPRING_PROFILES_ACTIVE=prod` (disables devtools, enables Flyway)
- Set `logging.level.root=WARN` in production

See [docs/guides/production-deployment.md](docs/guides/production-deployment.md) for detailed instructions.

---

## 🤝 Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) to get started.

---

## 🔒 Security

Please read our [SECURITY.md](SECURITY.md) for how to report security vulnerabilities responsibly.

---

## 📄 License

MIT © [Prakhar Tripathi](https://github.com/praakhartripathi)
