#!/usr/bin/env bash
# =============================================================================
# scripts/generate-jwt-secret.sh — Generate a cryptographically secure JWT secret
# Usage: bash scripts/generate-jwt-secret.sh
# =============================================================================
set -euo pipefail

SECRET=$(openssl rand -base64 64 | tr -d '\n')
echo ""
echo "  Generated JWT Secret (copy to .env as JWT_SECRET):"
echo ""
echo "  JWT_SECRET=${SECRET}"
echo ""
echo "  ⚠️  Store this securely. Rotating it will invalidate all active sessions."
echo ""
