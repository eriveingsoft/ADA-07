# Read First
1. Documentación/Diagramas/ERS_EBOGE.md
2. docs/REPO_MAP.md
3. TASK_UNDER_TEST.md

# Sources of Truth
Requirements live only in `Documentación/Diagramas/ERS_EBOGE.md`.
Do not change requirements merely to make tests pass.

# Repository Map
- `src/controlador/`: Controllers coordinating views and model (MVC)
- `src/modelo/`: Core business logic (MVC)
- `src/vista/`: JavaFX UI components (MVC)
- `test/`: JUnit test suite
- `scripts/`: Deterministic checks and build scripts

## Standard Commands
Verify: `./scripts/verify_repo.ps1`

## Change Rules
- Keep changes scoped to the requested task.
- Strictly adhere to the MVC architecture; do not mix business logic in the UI.
- Do NOT use absolute local paths (e.g., `C:\Users\...`) for compilation or execution. Use relative paths and the provided scripts.
- Do not introduce new dependencies without human justification.

## Stop Conditions
Stop and ask for human input when:
- requirements conflict,
- an architecture boundary must change,
- a required external system or library is unavailable.

# Definition of Done
- Validation script (`verify_repo`) passes cleanly.
- No absolute paths are hardcoded.
- Evidence of testing is provided.