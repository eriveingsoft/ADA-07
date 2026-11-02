# Independent Re-Review Report

## Re-Review Context
- **Change ID**: CR-02 (Match Exit to Main Menu & Resource Sync)
- **Branch / PR Candidate**: `ada09/cr-02`
- **Base Commit**: `cc71952` (`chore: fix verify runner script encoding for Windows PowerShell`)
- **Fix Commit Under Re-Review**: `2f910cf` (`fix: address ADA-09 review findings`)
- **Reviewer Agent**: `independent-reviewer-agent`
- **Date**: 2026-11-02
- **Tools & Permissions**: Read-only codebase inspection (`run_command`, `view_file`)

## Requirements & Acceptance Criteria
- **FR-16 (Match Exit to Main Menu - CR-02)**: The system must provide a mechanism during an active match to abandon the game and return the user cleanly to the main menu screen (`Inicio.fxml`), resetting active match state without terminating the application process.
- **AC-16 (relates to FR-16)**: Given an active match view, when the "Salir al Menú" action is triggered, then `ControladorPrincipal` resets the match state and navigates back to `Inicio.fxml`. In addition, the repository build mechanism must synchronize all view assets (`.fxml` and `.css`) to the runtime directory (`EBOGE/bin/Vista/`) so that all interface components render properly.

## Verification Evidence Reviewed
1. `results/verification-report.md`:
   - Repository contract (`python scripts/check_repo_contract.py`): PASS (Exit code 0).
   - Java MVC compilation (`javac -d EBOGE/bin [all .java]`): PASS (0 compiler errors).
   - Unit test suite (`RunRestartTests` via JUnit 5 Platform Launcher): PASS (9 tests found, 9 started, 9 successful, 0 failed, 676 ms).
   - Asset synchronization (`scripts/verify_repo.ps1` lines 69–84): PASS.
   - Working tree clean / `.gitignore` active: PASS.
2. `results/human-review-decisions.md`:
   - Human decisions on RF-01 (ACCEPTED), RF-02 (ACCEPTED), RF-03 (ACCEPTED), RF-04 (ACCEPTED), and RF-05 (DEFERRED).
3. `results/coder-report.md` (Fix Cycle Section):
   - Detailed rationale and mapping of review fixes inspected.
4. Independent Command & Test Execution:
   - Full `./scripts/verify_repo.ps1` execution verified: PASS (`VERIFY REPO: PASS`).
   - Repository contract verified independently: PASS.
   - JUnit test execution (5 baseline `ReiniciarPartidaTest` + 4 new `SalirPartidaTest`): 9/9 PASS.

---

## Findings Status Evaluation

### RF-01 — Architecture Boundary Violation / Binary Pollution
- **Original Severity**: MUST FIX
- **Human Decision**: ACCEPTED
- **Status**: **RESOLVED**
- **Evidence**:
  1. Commit `2f910cf` untracked 121 compiled `.class` files and binary assets under `EBOGE/bin/` via `git rm -r --cached EBOGE/bin/`.
  2. Root `.gitignore` was introduced with lines:
     ```gitignore
     # Build outputs and transient binaries
     EBOGE/bin/
     bin/
     *.class
     ```
  3. `git ls-files EBOGE/bin/` confirms zero tracked files under `EBOGE/bin/`.
  4. Executed `./scripts/verify_repo.ps1`, generating fresh compiled classes and synchronizing view assets into `EBOGE/bin/Vista/`. Post-build `git status` verifies that `EBOGE/bin/` remains untracked and does not dirty the Git index.
- **Conclusion**: Architectural boundary defined in `ARCHITECTURE.md` is fully restored. Build outputs are strictly transient.

---

### RF-02 — Test Assertion False Confidence in SalirPartidaTest
- **Original Severity**: SHOULD FIX
- **Human Decision**: ACCEPTED
- **Status**: **RESOLVED**
- **Evidence**:
  1. `EBOGE/src/controlador/ControladorPrincipal.java` (lines 188–190) now provides an explicit state injection method:
     ```java
     public void setPartida(Partida partida) {
         this.partida = partida;
     }
     ```
  2. `EBOGE/test/controlador/SalirPartidaTest.java` was updated in `testControladorPrincipalSalirAlMenuPrincipal`:
     ```java
     59: cp.setPartida(partida);
     60: 
     61: // Verificamos estado activo antes de salir (evita falsa confianza - RF-02)
     62: assertNotNull(cp.getPartida(), "La partida activa no debe ser nula antes de salir");
     ...
     66: cp.salirAlMenuPrincipal();
     67: 
     68: assertNull(cp.getPartida(), "La partida activa debe ser nula tras salir al menú principal");
     ```
  3. Updated `testControladorVentanaDePartidaDelegaSalida`:
     ```java
     79: cp.setPartida(partida);
     ...
     83: assertNotNull(cp.getPartida(), "La partida activa debe estar presente antes de delegar la salida");
     84: 
     85: cvp.salirAlMenuPrincipal();
     86: 
     87: assertNull(cp.getPartida(), "El ControladorPrincipal debe recibir la orden de salir y limpiar partida");
     ```
- **Conclusion**: The tests now definitively verify an active, non-null match instance transitioning to null. Eliminates vacuous passes and guarantees regression protection.

---

### RF-03 — Victory Dialog JVM Termination Defeating Process Preservation
- **Original Severity**: SHOULD FIX
- **Human Decision**: ACCEPTED
- **Status**: **RESOLVED**
- **Evidence**:
  1. In `EBOGE/src/controlador/ControladorMazosYTurnos.java`, lines 146–150 added delegation to `ControladorPrincipal`:
     ```java
     public void salirAlMenuPrincipal() {
         if (controladorPrincipal != null) {
             controladorPrincipal.salirAlMenuPrincipal();
         }
     }
     ```
  2. In `ControladorMazosYTurnos.java`, lines 235–238 refactored `mostrarDialogoVictoria()`:
     ```java
     } else if (respuesta.isPresent() && respuesta.get() == btnSalir) {
         salirAlMenuPrincipal();
     }
     ```
  3. Hard process exits (`Platform.exit(); System.exit(0);`) were completely removed from the victory handling workflow.
- **Conclusion**: Dismissing or clicking "Salir" on the victory dialog now returns cleanly to the main menu without killing the application JVM process, satisfying `FR-16`.

---

### RF-04 — PR Candidate Diff Base Alignment and Commit Scoping
- **Original Severity**: SHOULD FIX
- **Human Decision**: ACCEPTED
- **Status**: **RESOLVED**
- **Evidence**:
  1. Base branch alignment: Local `main` branch was fast-forwarded to `cc71952`.
  2. Noise reduction: The 8 historical ADA-08 commits were successfully eliminated from `results/commits.txt`.
  3. Clean mergeability: The PR diff and commit list strictly reflect CR-02 and its review fixes without out-of-scope commits.
- **Conclusion**: The branch in git (`ada09/cr-02` against `main`) is properly scoped and cleanly mergeable.

---

### RF-05 — Missing Match Exit Confirmation Dialog
- **Original Severity**: OPTIONAL
- **Human Decision**: DEFERRED
- **Status**: **RESOLVED (DEFERRED)**
- **Evidence**:
  - Formally recorded as deferred technical debt in `results/human-review-decisions.md` (lines 31–35) and acknowledged in `results/coder-report.md` (Known Limitations).
- **Conclusion**: Deferred per human governance; does not block CR-02 merge.

---

## Regression & Architecture Boundary Checks

| Check | Method | Result | Notes |
|---|---|---|---|
| Model UI isolation | `Select-String "javafx\." EBOGE/src/modelo/*.java` | PASS | 0 references to JavaFX in domain model |
| Absolute path check | `Select-String "C:\\Users" EBOGE/src/*.java, EBOGE/test/*.java` | PASS | 0 hardcoded absolute file paths |
| Baseline test suite | `ReiniciarPartidaTest` (JUnit 5) | PASS | 5/5 baseline tests passing |
| New test suite | `SalirPartidaTest` (JUnit 5) | PASS | 4/4 tests passing with genuine state assertions |
| Repo contract | `python scripts/check_repo_contract.py` | PASS | Exit code 0 |
| Headless safety | Static inspection of `mostrarVentanaDeInicio()` | PASS | Guard `if (ventanaPrincipal == null) return;` intact |

No new MUST FIX or SHOULD FIX regressions were introduced by the fix cycle.

---

## Status Summary

| Finding | Original Severity | Human Decision | Re-Review Status | Notes |
|---|---|---|---|---|
| **RF-01** | MUST FIX | ACCEPTED | **RESOLVED** | `EBOGE/bin/` untracked and ignored via `.gitignore` |
| **RF-02** | SHOULD FIX | ACCEPTED | **RESOLVED** | `setPartida` seam + explicit non-null/null assertions |
| **RF-03** | SHOULD FIX | ACCEPTED | **RESOLVED** | `System.exit(0)` removed; clean exit via controller |
| **RF-04** | SHOULD FIX | ACCEPTED | **RESOLVED** | Branch cleanly aligned; PR diff strictly scoped to CR-02 |
| **RF-05** | OPTIONAL | DEFERRED | **RESOLVED (DEFERRED)** | Documented technical debt per human decision |

- **Open MUST FIX Findings**: 0
- **Open Blocking Issues**: 0

---

## Reviewer Recommendation

- [ ] NOT READY
- [ ] READY AFTER MUST FIX
- [x] **READY FOR HUMAN MERGE**

*The PR candidate branch `ada09/cr-02` fulfills requirements `FR-16` and `AC-16`, respects the MVC architecture and repository contract, introduces no regressions, and resolves all blocking findings. The change is ready for human merge.*
