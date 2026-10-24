from pathlib import Path
import sys

# Lista de archivos que el agente NUNCA debe borrar y siempre debe consultar
required_files = [
    "AGENTS.md",
    "README.md",
    "Documentación/Diagramas/ERS_EBOGE.md",
    "docs/REPO_MAP.md",
    "TASK_UNDER_TEST.md",
    "scripts/verify_repo.ps1"
]

missing = [path for path in required_files if not Path(path).exists()]

if missing:
    print("REPO CONTRACT: FAIL")
    for path in missing:
        print(f"Missing: {path}")
    sys.exit(1)

print("REPO CONTRACT: PASS")
sys.exit(0)