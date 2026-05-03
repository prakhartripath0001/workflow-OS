<div align="center">

<h1>⚡ Workflow OS</h1>

<p>A modern, cross-platform desktop application built with <strong>Electron + React</strong> on the frontend and <strong>Spring Boot</strong> on the backend.</p>

<p>
  <img alt="Java" src="https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk&logoColor=white" />
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.2.x-6DB33F?style=flat-square&logo=spring-boot&logoColor=white" />
  <img alt="Electron" src="https://img.shields.io/badge/Electron-31-47848F?style=flat-square&logo=electron&logoColor=white" />
  <img alt="React" src="https://img.shields.io/badge/React-18-61DAFB?style=flat-square&logo=react&logoColor=black" />
  <img alt="Vite" src="https://img.shields.io/badge/Vite-5-646CFF?style=flat-square&logo=vite&logoColor=white" />
  <img alt="Tailwind CSS" src="https://img.shields.io/badge/Tailwind%20CSS-3-38B2AC?style=flat-square&logo=tailwind-css&logoColor=white" />
  <img alt="License" src="https://img.shields.io/badge/License-MIT-blue?style=flat-square" />
</p>

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [Available Scripts](#-available-scripts)
- [API Reference](#-api-reference)
- [Production Build](#-production-build)
- [Tech Stack](#-tech-stack)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🌟 Overview

**Workflow OS** is a cross-platform desktop application that combines the power of a **Spring Boot** REST API backend with an **Electron**-wrapped **React** frontend. It provides a native desktop experience on macOS, Windows, and Linux while communicating with a locally running Java backend.

Key highlights:
- 🖥️ **Native desktop app** via Electron (macOS `.app`, Windows `.exe`, Linux `.AppImage`)
- ⚡ **Blazing fast dev experience** with Vite HMR + Spring Boot DevTools
- 🎨 **Dark glassmorphism UI** built with Tailwind CSS
- 🔒 **Secure IPC** with Electron contextBridge (no nodeIntegration)
- 🩺 **Live health dashboard** with auto-refresh every 30 seconds

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────┐
│                    Desktop (Electron)                     │
│  ┌─────────────────────────────────────────────────────┐ │
│  │              React App (Vite + Tailwind)             │ │
│  │                                                     │ │
│  │   Dashboard → Workflows → Analytics → Settings      │ │
│  └─────────────────────┬───────────────────────────────┘ │
│                        │  HTTP (axios)                    │
└────────────────────────┼─────────────────────────────────┘
                         │
             ┌───────────▼──────────┐
             │   Spring Boot API    │
             │   localhost:8080     │
             │                     │
             │  GET /api/health     │
             │  GET /api/info       │
             │  GET /actuator/...   │
             └──────────────────────┘
```

---

## 📁 Project Structure

```
workflow-OS/
│
├── backend/                           # Spring Boot backend (Java 17, Maven)
│   ├── src/
│   │   └── main/
│   │       ├── java/com/workflowos/
│   │       │   ├── WorkflowOsApplication.java   # Entry point
│   │       │   ├── controller/
│   │       │   │   └── HealthController.java     # REST endpoints
│   │       │   └── config/
│   │       │       └── WebConfig.java            # CORS configuration
│   │       └── resources/
│   │           └── application.properties        # Server config
│   └── pom.xml                                   # Maven dependencies
│
├── frontend/                          # Electron + React frontend
│   ├── electron/
│   │   ├── main.js                   # Electron main process
│   │   └── preload.js               # Secure contextBridge
│   ├── src/
│   │   ├── App.jsx                  # Main React component
│   │   ├── main.jsx                 # React root mount
│   │   └── index.css               # Tailwind + global styles
│   ├── index.html                   # HTML entry point
│   ├── vite.config.js              # Vite configuration
│   ├── tailwind.config.js          # Tailwind CSS config
│   ├── postcss.config.js           # PostCSS config
│   └── package.json                # Node dependencies & scripts
│
├── EXECUTION_FLOW.txt               # Step-by-step macOS startup guide
└── README.md                        # This file
```

---

## ✅ Prerequisites

| Tool | Version | Check | Install (macOS) |
|------|---------|-------|-----------------|
| Java | 17 (LTS) | `java -version` | `brew install --cask temurin@17` |
| Maven | 3.9+ | `mvn -version` | `brew install maven` |
| Node.js | 20+ | `node -v` | `brew install node` |
| npm | 10+ | `npm -v` | Included with Node.js |
| Git | Any | `git --version` | `brew install git` |

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone <your-repo-url> workflow-OS
cd workflow-OS
```

### 2. Start the backend

```bash
cd backend
mvn spring-boot:run
```

> The API starts on **http://localhost:8080**. Keep this terminal open.

### 3. Install frontend dependencies *(first time only)*

```bash
cd frontend
npm install
```

### 4. Launch the Electron app

```bash
npm run electron:dev
```

> An Electron window opens. If the backend is running, the dashboard shows **"Backend Online"**.

---

## 📜 Available Scripts

### Backend (`/backend`)

| Command | Description |
|---------|-------------|
| `mvn spring-boot:run` | Start the dev server with hot-reload |
| `mvn clean package` | Build the production JAR |
| `mvn test` | Run all unit tests |

### Frontend (`/frontend`)

| Script | Description |
|--------|-------------|
| `npm run dev` | Start Vite dev server only (no Electron) |
| `npm run electron:dev` | Start Vite + launch Electron (full dev mode) |
| `npm run build` | Build the React app to `dist/` |
| `npm run electron:build` | Build the distributable Electron app |
| `npm run preview` | Preview the Vite production build |

---

## 📡 API Reference

### `GET /api/health`

Returns the application health status.

**Response:**
```json
{
  "status":    "UP",
  "service":   "workflow-os-backend",
  "version":   "1.0.0",
  "timestamp": "2026-05-03T17:00:00.000000"
}
```

---

### `GET /api/info`

Returns app metadata and stack info.

**Response:**
```json
{
  "app": "Workflow OS",
  "description": "Cross-platform workflow automation desktop app",
  "stack": {
    "backend":  "Spring Boot 3.2 / Java 17",
    "frontend": "Electron + React 18 + Vite + Tailwind CSS"
  }
}
```

---

### `GET /actuator/health`

Spring Boot Actuator health endpoint.

---

## 📦 Production Build

### Backend JAR

```bash
cd backend
mvn clean package -DskipTests
java -jar target/backend-1.0.0.jar
```

### Electron Desktop App (macOS)

```bash
cd frontend
npm run electron:build
```

Output is in `frontend/dist-electron/`. The macOS `.app` bundle is self-contained and can be moved to `/Applications`.

---

## 🛠️ Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Desktop shell | [Electron](https://www.electronjs.org/) | 31 |
| Frontend framework | [React](https://react.dev/) | 18 |
| Build tool | [Vite](https://vitejs.dev/) | 5 |
| Styling | [Tailwind CSS](https://tailwindcss.com/) | 3 |
| HTTP client | [Axios](https://axios-http.com/) | 1.7 |
| Backend framework | [Spring Boot](https://spring.io/projects/spring-boot) | 3.2.x |
| Language (backend) | Java | 17 |
| Build tool (backend) | [Maven](https://maven.apache.org/) | 3.9+ |
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

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">
  <p>Built with ❤️ using Electron, React, and Spring Boot</p>
</div>
