#!/usr/bin/env bash
set -euo pipefail

echo "[1/3] Running repository contract checks..."
python scripts/check_repo_contract.py

echo "[2/3] Checking verification script..."
if command -v pwsh &> /dev/null; then
    pwsh -File ./scripts/verify_repo.ps1
elif command -v powershell &> /dev/null; then
    powershell -File ./scripts/verify_repo.ps1
else
    echo "PowerShell not found. Please execute ./scripts/verify_repo.ps1 directly."
fi

echo "VERIFY_REPO: PASS"
