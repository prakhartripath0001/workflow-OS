<div align="center">

<h1>⚡ Workflow OS</h1>

<p>A modern, cross-platform desktop application built with <strong>Electron + React</strong> on the frontend and <strong>Spring Boot + PostgreSQL</strong> on the backend.</p>

<p>
  <img alt="Java" src="https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk&logoColor=white" />
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.2.x-6DB33F?style=flat-square&logo=spring-boot&logoColor=white" />
  <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql&logoColor=white" />
  <img alt="Electron" src="https://img.shields.io/badge/Electron-31-47848F?style=flat-square&logo=electron&logoColor=white" />
  <img alt="React" src="https://img.shields.io/badge/React-18-61DAFB?style=flat-square&logo=react&logoColor=black" />
  <img alt="Vite" src="https://img.shields.io/badge/Vite-5-646CFF?style=flat-square&logo=vite&logoColor=white" />
  <img alt="Tailwind CSS" src="https://img.shields.io/badge/Tailwind%20CSS-3-38B2AC?style=flat-square&logo=tailwind-css&logoColor=white" />
  <img alt="Docker" src="https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white" />
  <img alt="License" src="https://img.shields.io/badge/License-MIT-blue?style=flat-square" />
</p>

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Prerequisites](#-prerequisites)
- [Getting Started — Local](#-getting-started--local)
- [Getting Started — Docker](#-getting-started--docker)
- [Available Scripts](#-available-scripts)
- [API Reference](#-api-reference)
- [Production Build](#-production-build)
- [Tech Stack](#-tech-stack)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🌟 Overview

**Workflow OS** is a cross-platform desktop application combining a **Spring Boot** REST API (backed by **PostgreSQL**) with an **Electron**-wrapped **React** frontend. It provides a native desktop experience on macOS, Windows, and Linux while communicating with a locally running or Dockerized Java backend.

Key highlights:
- 🖥️ **Native desktop app** via Electron (macOS `.app`, Windows `.exe`, Linux `.AppImage`)
- 🐘 **PostgreSQL 16** database with normalized schema, ENUMs, indexes, and auto-`updated_at` triggers
- 🐳 **Docker Compose** — one command spins up postgres + backend + React/nginx frontend
- ⚡ **Blazing fast dev experience** with Vite HMR + Spring Boot DevTools
- 🎨 **Dark glassmorphism UI** built with Tailwind CSS
- 🔒 **Secure IPC** with Electron contextBridge (no `nodeIntegration`)
- 🩺 **Live health dashboard** with auto-refresh every 30 seconds

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│                      Desktop (Electron — native)                      │
│  ┌──────────────────────────────────────────────────────────────────┐│
│  │                  React App (Vite + Tailwind CSS)                  ││
│  │                                                                   ││
│  │       Dashboard → Workflows → Analytics → Settings               ││
│  └──────────────────────────────┬────────────────────────────────────┘│
│                                 │  HTTP/axios                          │
└─────────────────────────────────┼────────────────────────────────────┘
                                  │
                     ┌────────────▼────────────┐
                     │    Spring Boot API       │
                     │    localhost:8080        │
                     │                         │
                     │  GET  /api/health        │
                     │  GET  /api/info          │
                     │  GET  /api/workflows     │
                     │  GET  /api/workflows/:id │
                     │  POST /api/workflows     │
                     └────────────┬────────────┘
                                  │  JDBC (HikariCP)
                     ┌────────────▼────────────┐
                     │     PostgreSQL 16        │
                     │     port 5432           │
                     │                         │
                     │  users                  │
                     │  workflows              │
                     │  workflow_tasks         │
                     └─────────────────────────┘

──── Docker Compose boundary ──────────────────────────────────────────
  postgres ──healthcheck──▶ backend ──healthcheck──▶ frontend (nginx:3000)
```

---

## 📁 Project Structure

```
workflow-OS/
│
├── backend/                               # Spring Boot backend (Java 17, Maven)
│   ├── src/main/
│   │   ├── java/com/workflowos/
│   │   │   ├── WorkflowOsApplication.java        # Entry point
│   │   │   ├── entity/
│   │   │   │   ├── User.java                     # JPA entity
│   │   │   │   ├── Workflow.java                 # JPA entity
│   │   │   │   └── WorkflowTask.java             # JPA entity
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── WorkflowRepository.java
│   │   │   │   └── WorkflowTaskRepository.java
│   │   │   ├── controller/
│   │   │   │   ├── HealthController.java          # GET /api/health, /api/info
│   │   │   │   └── WorkflowController.java        # Workflow CRUD endpoints
│   │   │   └── config/
│   │   │       └── WebConfig.java                 # CORS configuration
│   │   └── resources/
│   │       ├── db/
│   │       │   ├── schema.sql                     # ★ DDL — tables, ENUMs, triggers
│   │       │   └── data.sql                       # ★ Seed data (idempotent)
│   │       └── application.properties             # Server + DB config (env-var driven)
│   ├── Dockerfile                                 # Multi-stage Maven → JRE build
│   └── pom.xml
│
├── frontend/                              # Electron + React frontend
│   ├── electron/
│   │   ├── main.js                       # Electron main process (dev + prod)
│   │   └── preload.js                   # Secure contextBridge
│   ├── src/
│   │   ├── App.jsx                      # Main React component (dashboard UI)
│   │   ├── main.jsx                     # React root mount
│   │   └── index.css                   # Tailwind + global styles
│   ├── index.html
│   ├── nginx.conf                        # SPA routing + API proxy + gzip
│   ├── Dockerfile                        # Multi-stage Vite build → nginx
│   ├── vite.config.js
│   ├── tailwind.config.js
│   ├── postcss.config.js
│   └── package.json
│
├── docker-compose.yml                    # ★ One-command full-stack startup
├── .env.example                          # Environment variable template
├── EXECUTION_FLOW.txt                    # Step-by-step macOS startup guide
└── README.md
```

---

## 🗄️ Database Schema

PostgreSQL 16 with three normalized tables, ENUM types, and auto-`updated_at` triggers.

```
┌──────────────┐       ┌──────────────────────┐       ┌───────────────────────┐
│    users     │ 1   N │      workflows        │ 1   N │    workflow_tasks     │
│──────────────│───────│──────────────────────│───────│───────────────────────│
│ id (UUID PK) │       │ id (UUID PK)         │       │ id (UUID PK)          │
│ name         │       │ name                 │       │ workflow_id (FK)       │
│ email (UQ)   │       │ description          │       │ title                 │
│ avatar_url   │       │ status* (ENUM)       │       │ description           │
│ created_at   │       │ owner_id (FK→users)  │       │ status** (ENUM)       │
│ updated_at   │       │ created_at           │       │ position (sort order) │
└──────────────┘       │ updated_at           │       │ created_at            │
                       └──────────────────────┘       │ updated_at            │
                                                       └───────────────────────┘

* workflow_status  : DRAFT | ACTIVE | PAUSED | COMPLETED | ARCHIVED
** task_status     : TODO  | IN_PROGRESS | BLOCKED | DONE | CANCELLED
```

**SQL files:**

| File | Purpose |
|------|---------|
| `backend/src/main/resources/db/schema.sql` | DDL — creates all tables, ENUMs, indexes, triggers |
| `backend/src/main/resources/db/data.sql` | Seed — 2 users, 3 workflows, 8 tasks (idempotent via `ON CONFLICT DO NOTHING`) |

Both files are executed automatically by Spring Boot on startup (`spring.sql.init.mode=always`).

---

## ✅ Prerequisites

### Local development

| Tool | Version | Check | Install (macOS) |
|------|---------|-------|-----------------|
| Java | 17 (LTS) | `java -version` | `brew install --cask temurin@17` |
| Maven | 3.9+ | `mvn -version` | `brew install maven` |
| Node.js | 20+ | `node -v` | `brew install node` |
| PostgreSQL | 16+ | `psql --version` | `brew install postgresql@16` |

### Docker mode (replaces Java + PostgreSQL requirement)

| Tool | Version | Check | Install (macOS) |
|------|---------|-------|-----------------|
| Docker Desktop | Latest | `docker -v` | [docker.com](https://www.docker.com/products/docker-desktop) |
| Node.js | 20+ | `node -v` | `brew install node` *(for Electron only)* |

---

## 🚀 Getting Started — Local (no Docker)

### 1. Clone

```bash
git clone <your-repo-url> workflow-OS
cd workflow-OS
```

### 2. Start PostgreSQL and create the database

```bash
psql -U postgres -c "CREATE DATABASE workflowos;"
psql -U postgres -c "CREATE USER workflowos WITH PASSWORD 'workflowos';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE workflowos TO workflowos;"
```

### 3. Start the Spring Boot backend

```bash
cd backend
mvn spring-boot:run
# Spring Boot auto-runs schema.sql → data.sql on first boot
# API starts on http://localhost:8080
```

### 4. Install frontend dependencies *(first time only)*

```bash
cd ../frontend && npm install
```

### 5. Launch the Electron desktop app

```bash
npm run electron:dev
# Opens the native desktop window pointing to http://localhost:8080
```

---

## 🐳 Getting Started — Docker + Electron *(recommended)*

This is the **fastest way** to get everything running. Docker handles the database and backend; Electron runs natively on your Mac for the desktop window.

### Step 1 — Copy environment variables

```bash
cd workflow-OS
cp .env.example .env
# Edit .env if you want custom passwords (optional)
```

### Step 2 — Start backend services with Docker

```bash
docker compose up -d --build
```

This will:
1. 🐘 Start **PostgreSQL 16** on port `5432`
2. ☕ Build and start the **Spring Boot** backend on port `8080`  
   *(runs `schema.sql` + `data.sql` automatically — creates tables and demo accounts)*
3. ⏳ Wait for health checks to pass before proceeding

> **Check everything is healthy:**
> ```bash
> docker compose ps
> # All three services should show "healthy"
> ```

### Step 3 — Install frontend dependencies *(first time only)*

```bash
cd frontend
npm install
```

### Step 4 — Launch the Electron desktop app

```bash
npm run electron:dev
```

A **native desktop window** will open. You will see the **Login / Sign Up** screen.

### Step 5 — Sign in

Use the pre-seeded demo accounts:

| Email | Password |
|-------|----------|
| `alice@workflowos.dev` | `password123` |
| `bob@workflowos.dev` | `password123` |

Or click **Sign Up** to create your own account.

---

### 🔗 Service URLs (when Docker is running)

| URL | Description |
|-----|-------------|
| `http://localhost:3000` | React app served by nginx (web browser) |
| `http://localhost:8080/api/health` | Spring Boot health check |
| `http://localhost:8080/api/auth/login` | Auth endpoint |
| `localhost:5432` | PostgreSQL (use any DB client) |

### 🛠️ Useful Docker commands

```bash
docker compose up -d --build    # Start in background (rebuilds images)
docker compose logs -f          # Stream all logs
docker compose logs -f backend  # Backend logs only
docker compose ps               # Check service health
docker compose down             # Stop all containers
docker compose down -v          # Stop + wipe database volume (fresh start)
```

---

### ❓ Troubleshooting

**The app shows a black screen?**
→ Make sure Docker containers are running: `docker compose ps`  
→ Then run `npm run electron:dev` from the `frontend/` directory.

**Login fails with "connection refused"?**
→ Backend isn't ready yet. Run `docker compose logs -f backend` and wait for `Started WorkflowOsApplication`.

**"Email already exists" on signup?**
→ Use a different email, or `docker compose down -v` to reset the database.

**Port 8080 already in use?**
→ Edit `.env` and change `BACKEND_PORT=8081`, then update the API URL in `frontend/src/pages/AuthPage.jsx`.

---

## 📜 Available Scripts

### Backend (`/backend`)

| Command | Description |
|---------|-------------|
| `mvn spring-boot:run` | Start dev server with hot-reload |
| `mvn clean package -DskipTests` | Build production JAR |
| `mvn test` | Run unit tests |

### Frontend (`/frontend`)

| Script | Description |
|--------|-------------|
| `npm run dev` | Vite dev server only (no Electron) |
| `npm run electron:dev` | Vite + Electron (full dev mode) |
| `npm run build` | Build React app to `dist/` |
| `npm run electron:build` | Build distributable `.app` / `.exe` |
| `npm run preview` | Preview Vite production build |

---

## 📡 API Reference

### `GET /api/health`

```json
{
  "status":    "UP",
  "service":   "workflow-os-backend",
  "version":   "1.0.0",
  "timestamp": "2026-05-03T17:00:00"
}
```

### `GET /api/info`

```json
{
  "app": "Workflow OS",
  "description": "Cross-platform workflow automation desktop app",
  "stack": { "backend": "Spring Boot 3.2 / Java 17", "frontend": "Electron + React 18" }
}
```

### `GET /api/workflows`

Returns all workflows (array).

### `GET /api/workflows/{id}`

Returns a single workflow by UUID.

### `GET /api/workflows/{id}/tasks`

Returns all tasks for a workflow, ordered by `position ASC`.

### `POST /api/workflows`

Create a new workflow. Body: `{ "name": "...", "description": "...", "owner": { "id": "..." } }`

### `DELETE /api/workflows/{id}`

Delete a workflow (cascades to tasks).

### `GET /actuator/health`

Spring Boot Actuator health (includes DB connectivity).

---

## 📦 Production Build

### Docker (recommended)

```bash
docker compose up --build -d
```

### Manual JAR

```bash
cd backend && mvn clean package -DskipTests
java -jar target/backend-1.0.0.jar
```

### Electron `.app` (macOS)

```bash
cd frontend && npm run electron:build
# Output: frontend/dist-electron/Workflow OS.app
```

---

## 🛠️ Tech Stack

| Layer | Technology | Version |
|-------|-----------|----|
| Desktop shell | [Electron](https://www.electronjs.org/) | 31 |
| Frontend framework | [React](https://react.dev/) | 18 |
| Build tool | [Vite](https://vitejs.dev/) | 5 |
| Styling | [Tailwind CSS](https://tailwindcss.com/) | 3 |
| HTTP client | [Axios](https://axios-http.com/) | 1.7 |
| Web server | [nginx](https://nginx.org/) | 1.27 |
| Backend framework | [Spring Boot](https://spring.io/projects/spring-boot) | 3.2.x |
| Language | Java | 17 |
| ORM | Spring Data JPA / Hibernate | — |
| Database | [PostgreSQL](https://www.postgresql.org/) | 16 |
| Build tool | [Maven](https://maven.apache.org/) | 3.9+ |
| Containerization | [Docker Compose](https://docs.docker.com/compose/) | v2 |
| Monitoring | Spring Boot Actuator | — |

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'feat: add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

Please follow [Conventional Commits](https://www.conventionalcommits.org/) for commit messages.

---

## 📄 License

This project is licensed under the **MIT License**.

---

<div align="center">
  <p>Built with ❤️ using Electron, React, Spring Boot, PostgreSQL & Docker</p>
</div>
