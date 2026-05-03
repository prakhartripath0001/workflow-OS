#!/bin/bash
# =============================================================================
#  Workflow OS — One-command startup script (macOS)
#  Usage: ./start.sh   OR   npm start (from project root)
# =============================================================================

set -e  # exit on any error

# ── Colors ───────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BOLD='\033[1m'; RESET='\033[0m'

log()  { echo -e "${CYAN}[workflow-os]${RESET} $1"; }
ok()   { echo -e "${GREEN}[✓]${RESET} $1"; }
warn() { echo -e "${YELLOW}[!]${RESET} $1"; }
fail() { echo -e "${RED}[✗]${RESET} $1"; exit 1; }

echo ""
echo -e "${BOLD}⚡ Workflow OS — Starting...${RESET}"
echo "──────────────────────────────────────────────"

# ── 1. Check Docker is running ────────────────────────────────────────────────
log "Checking Docker..."
if ! docker info > /dev/null 2>&1; then
  fail "Docker is not running. Please start Docker Desktop and try again."
fi
ok "Docker is running"

# ── 2. Check Node.js ──────────────────────────────────────────────────────────
log "Checking Node.js..."
if ! command -v node > /dev/null 2>&1; then
  fail "Node.js not found. Install via: brew install node"
fi
ok "Node.js $(node -v) found"

# ── 3. Install frontend dependencies if needed ────────────────────────────────
if [ ! -d "frontend/node_modules" ]; then
  log "Installing frontend dependencies (first run)..."
  cd frontend && npm install --prefer-offline && cd ..
  ok "Frontend dependencies installed"
else
  ok "Frontend dependencies already installed"
fi

# ── 4. Copy .env if missing ───────────────────────────────────────────────────
if [ ! -f ".env" ]; then
  warn ".env not found — copying from .env.example"
  cp .env.example .env
  ok ".env created (default values)"
fi

# ── 5. Start Docker services (postgres + backend) ─────────────────────────────
log "Starting Docker services (postgres + backend)..."
docker compose up -d --build
ok "Docker services started"

# ── 6. Wait for Spring Boot backend to be healthy ────────────────────────────
log "Waiting for backend to be ready..."
RETRIES=30
COUNT=0
until curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; do
  COUNT=$((COUNT + 1))
  if [ "$COUNT" -ge "$RETRIES" ]; then
    fail "Backend did not become healthy in time.\nCheck logs with: docker compose logs backend"
  fi
  echo -ne "  ${YELLOW}⏳ Waiting... (${COUNT}/${RETRIES})${RESET}\r"
  sleep 3
done
echo ""
ok "Backend is healthy at http://localhost:8080"

# ── 7. Launch Electron desktop app ────────────────────────────────────────────
echo ""
echo -e "${BOLD}🖥️  Opening Workflow OS desktop app...${RESET}"
echo "──────────────────────────────────────────────"
echo -e "  ${CYAN}API${RESET}     → http://localhost:8080"
echo -e "  ${CYAN}Web UI${RESET}  → http://localhost:3000"
echo -e "  ${CYAN}DB${RESET}      → localhost:5432 (workflowos)"
echo ""

cd frontend && npm run electron:dev
