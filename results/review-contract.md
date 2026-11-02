# Review Contract

## Change
CR-02 (Match Exit to Main Menu & Resource Sync)

## Requirements / Acceptance Criteria
- **FR-16**: The system must provide a mechanism during an active match to abandon the game and return the user cleanly to the main menu screen (`Inicio.fxml`), resetting active match state without terminating the application process.
- **AC-16**: Given an active match view, when the "Salir al Menú" action is triggered, then `ControladorPrincipal` resets the match state and navigates back to `Inicio.fxml`. In addition, the repository build mechanism must synchronize all view assets (`.fxml` and `.css`) to the runtime directory (`EBOGE/bin/Vista/`) so that all interface components render properly.

## Architecture Constraints
- **Expected dependency flow**: View (`src/vista/`) -> Controller (`src/controlador/`) -> Model (`src/modelo/`). Controllers orchestrate screen transitions and lifecycle; Views emit UI events; Model maintains pure game entities.
- **Business-rule location**: Match lifecycle cleanup (clearing `partida`, `jugadoresPartida`, `carasDadoPartida`) must reside in `ControladorPrincipal`. View classes (`ControladorVentanaDePartida`) must delegate lifecycle state changes to `ControladorPrincipal` and not directly manipulate underlying models unsafely.
- **Protected boundaries**:
  - `src/modelo/` must never import `javafx.*` or UI packages.
  - No hardcoded absolute user file paths (`C:\Users\...`).
  - Runtime binaries under `EBOGE/bin/` must not be manually edited; they must be generated/synchronized by `scripts/verify_repo.ps1`.

## Review Rules
- Verify requirement coverage against `FR-16` and `AC-16`.
- Verify every called API and dependency exists in Java 21+ / JavaFX standard APIs.
- Check architecture boundaries: confirm no UI leakage into model and proper MVC delegation.
- Inspect tests for masking, false confidence, or missing edge cases (e.g. null checks, lifecycle cleanup).
- Check regression behavior: ensure existing tests (`ReiniciarPartidaTest`) remain 100% functional.
- Identify unnecessary technical debt, resource leaks, or missing confirmation dialogues.
- Cite concrete evidence (file, line number, diff context) for every finding.

## Severity
### MUST FIX:
Requirement violation, regression, security/integrity risk, hallucinated API/dependency, broken architecture constraint, or defect that makes the change unsafe to approve.

### SHOULD FIX:
Significant maintainability, coverage, design, or clarity issue that does not block the requested behavior.

### OPTIONAL:
Non-blocking improvement outside the minimum acceptance threshold.

