# PR Evidence

## Change
- **Change ID**: CR-02
- **PR / Branch**: `ada09/cr-02`
- **Base commit**: `cc71952` (`chore: fix verify runner script encoding for Windows PowerShell`)
- **Final commit**: `2f910cf` (`fix: address ADA-09 review findings`)

## Requirements / Acceptance Criteria
- **FR-16 (Match Exit to Main Menu)**: The system must provide a mechanism during an active match to abandon the game and return the user cleanly to the main menu screen (`Inicio.fxml`), resetting active match state without terminating the application process.
- **AC-16 (relates to FR-16)**: Given an active match view, when the "Salir al Menú" action is triggered, then `ControladorPrincipal` resets the match state and navigates back to `Inicio.fxml`. In addition, the repository build mechanism must synchronize all view assets (`.fxml` and `.css`) to the runtime directory (`EBOGE/bin/Vista/`) so that all interface components render properly.

## Implementation
- **Coder report**: `results/coder-report.md`
- **Files changed**:
  - `.gitignore`: Added to ignore transient build directory `EBOGE/bin/`.
  - `EBOGE/src/controlador/ControladorPrincipal.java`: Implemented `salirAlMenuPrincipal()`, `setPartida()`, and null-safe `mostrarVentanaDeInicio()`.
  - `EBOGE/src/controlador/ControladorVentanaDePartida.java`: Injected top-bar button `btnSalirMenu` and overlay button `btnSalirMenuOverlay` delegating to controller.
  - `EBOGE/src/controlador/ControladorMazosYTurnos.java`: Replaced `System.exit(0)` with clean delegation to `salirAlMenuPrincipal()`.
  - `EBOGE/src/vista/VentanaDePartida.fxml`: Added exit buttons in top bar and victory overlay.
  - `EBOGE/src/vista/Partida.css`: Added styles `.btn-salir-menu` and `.btn-salir-grande`.
  - `EBOGE/test/controlador/SalirPartidaTest.java`: Added 4 unit tests verifying lifecycle reset and controller delegation.
  - `scripts/verify_repo.ps1`: Automated FXML/CSS asset copying to `bin/Vista/` and included `SalirPartidaTest` in runner.
  - `REQUIREMENTS.md`, `SPEC.md`, `TASKS.md`, `CHANGE_REQUEST.md`: Kept sources of truth in sync.
- **Commits**:
  - `0c26716`: `feat: implement CR-02 match exit button and resource synchronization`
  - `2f910cf`: `fix: address ADA-09 review findings`

## Verification
- **Verification command**: `powershell -ExecutionPolicy Bypass -File ./scripts/verify_repo.ps1`
- **Final result**: `VERIFY REPO: PASS`
- **Tests**: 9 tests found, 9 started, 9 successful, 0 failed (ReiniciarPartidaTest: 5/5, SalirPartidaTest: 4/4)
- **Lint / Build**: 0 errors across all Java source packages
- **Security / repo checks**: Repository contract PASS; zero hardcoded absolute paths; zero UI leakage in domain model

## Independent Review
- **Review report**: `results/review-report.md`
- **MUST FIX found**: 1 (RF-01: Committed binary build artifacts in `EBOGE/bin/`)
- **SHOULD FIX found**: 3 (RF-02: Test assertion false confidence, RF-03: Victory dialog JVM termination, RF-04: PR diff base alignment)
- **OPTIONAL found**: 1 (RF-05: Missing exit confirmation dialog)

## Human Decisions
- **Accepted**: RF-01, RF-02, RF-03, RF-04
- **Modified**: 0
- **Rejected**: 0
- **Deferred**: RF-05 (Confirmation dialog recorded as technical debt)

## Re-review
- **Re-review report**: `results/re-review-report.md`
- **Resolved**: RF-01, RF-02, RF-03, RF-04
- **Partially resolved**: 0
- **Not resolved**: 0
- **New MUST FIX**: 0

## Traceability
| Requirement / AC | Task / Change | Code | Test | Review Finding |
|---|---|---|---|---|
| FR-16 / AC-16 | CR-02: Match Exit & Resource Sync | `ControladorPrincipal.java`, `ControladorVentanaDePartida.java`, `VentanaDePartida.fxml`, `Partida.css`, `verify_repo.ps1` | `SalirPartidaTest.java` (4 tests) | RF-01, RF-02, RF-03, RF-04 (All Resolved) |
| FR-13 / AC-13 | CR-01: Match Restart (Preserved) | `ControladorPrincipal.java`, `ControladorVentanaDePartida.java` | `ReiniciarPartidaTest.java` (5 tests) | Regression verified (0 regressions) |

## Remaining Risks / Technical Debt
- **RF-05**: The exit button immediately returns to menu without asking for confirmation. Deferred to next sprint backlog.
- **Headless FXML Loading**: `mostrarVentanaDeInicio()` is guarded against null `Stage` during headless CLI testing. While this ensures unit test stability, end-to-end visual styling should be validated interactively.

## Final Human Decision
- [ ] NOT READY
- [x] READY FOR HUMAN MERGE

Approved by: Erick Vega (Human Reviewer)
Date: 2026-11-02

