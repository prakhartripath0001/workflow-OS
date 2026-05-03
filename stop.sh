#!/bin/bash
# =============================================================================
#  Workflow OS — Stop all services
#  Usage: ./stop.sh   OR   npm stop
# =============================================================================
echo ""
echo "⚡ Workflow OS — Stopping..."
docker compose down
echo "✓ All services stopped."
echo ""
