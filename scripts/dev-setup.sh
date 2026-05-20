#!/usr/bin/env bash
# =============================================================================
# scripts/dev-setup.sh — One-command developer environment setup
# Usage: bash scripts/dev-setup.sh
# =============================================================================
set -euo pipefail

CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info()    { echo -e "${CYAN}[INFO]${NC} $1"; }
success() { echo -e "${GREEN}[OK]${NC}   $1"; }
warn()    { echo -e "${YELLOW}[WARN]${NC} $1"; }
error()   { echo -e "${RED}[ERR]${NC}  $1"; exit 1; }

echo ""
echo "  ⚡ SlashAI — Developer Setup"
echo "================================"
echo ""

# ── 1. Check prerequisites ───────────────────────────────────────────────────
info "Checking prerequisites..."

command -v docker   >/dev/null 2>&1 || error "Docker not found. Install: https://docker.com"
command -v node     >/dev/null 2>&1 || error "Node.js not found. Install: https://nodejs.org (v20+)"
command -v java     >/dev/null 2>&1 || error "Java not found. Install: https://adoptium.net (v17)"
command -v mvn      >/dev/null 2>&1 || error "Maven not found. Install: brew install maven"

NODE_VER=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
JAVA_VER=$(java -version 2>&1 | awk -F '"' '{print $2}' | cut -d'.' -f1)

[[ "$NODE_VER" -lt 20 ]] && warn "Node.js $NODE_VER detected — v20+ recommended"
[[ "$JAVA_VER" -lt 17 ]] && error "Java $JAVA_VER detected — Java 17 required"
success "Prerequisites satisfied"

# ── 2. Environment variables ─────────────────────────────────────────────────
info "Setting up environment..."
if [[ ! -f .env ]]; then
    cp .env.example .env
    warn ".env created from .env.example — fill in your OAuth credentials before running"
else
    success ".env already exists"
fi

# ── 3. Generate JWT secret if placeholder ────────────────────────────────────
if grep -q "REPLACE_WITH_SECURE_SECRET" .env 2>/dev/null; then
    JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')
    if [[ "$(uname)" == "Darwin" ]]; then
        sed -i '' "s|REPLACE_WITH_SECURE_SECRET_openssl_rand_base64_64|${JWT_SECRET}|" .env
    else
        sed -i "s|REPLACE_WITH_SECURE_SECRET_openssl_rand_base64_64|${JWT_SECRET}|" .env
    fi
    success "JWT_SECRET generated and saved to .env"
fi

# ── 4. Frontend dependencies ─────────────────────────────────────────────────
info "Installing frontend dependencies..."
cd frontend && npm install --prefer-offline && cd ..
success "Frontend npm install complete"

# ── 5. Start infrastructure ──────────────────────────────────────────────────
info "Starting PostgreSQL via Docker Compose..."
docker compose up -d postgres
info "Waiting for PostgreSQL to be ready..."
until docker compose exec postgres pg_isready -U "${WORKFLOWOS_POSTGRES_USER:-workflowos}" -d "${WORKFLOWOS_POSTGRES_DB:-workflowos}" >/dev/null 2>&1; do
    printf "."
    sleep 1
done
echo ""
success "PostgreSQL is ready"

# ── 6. Backend compile check ─────────────────────────────────────────────────
info "Validating backend compilation..."
cd backend && mvn compile -q --no-transfer-progress && cd ..
success "Backend compiles successfully"

# ── 7. Done ──────────────────────────────────────────────────────────────────
echo ""
echo "  ✅ Setup complete!"
echo ""
echo "  Start development:"
echo "    1. Backend:  cd backend && mvn spring-boot:run"
echo "    2. Frontend: cd frontend && npm run electron:dev"
echo ""
echo "  Or run everything in Docker:"
echo "    docker compose up -d"
echo ""
echo "  API Docs: http://localhost:8080/swagger-ui.html"
echo ""
