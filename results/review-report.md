# Independent Review Report

## Review Context
- **Change ID**: CR-02
- **Branch / PR**: `ada09/cr-02`
- **Commit reviewed**: `0c267164085bbeb6da9854687c577f2ec99c78ea` (`feat: implement CR-02 match exit button and resource synchronization`)
- **Reviewer agent**: `independent-reviewer-agent`
- **Model**: `gemini-2.5-pro`
- **Tools / permissions**: Read-only codebase access (`run_command` [read-only], `view_file`)

## Requirements Reviewed
- **FR-16 (Match Exit to Main Menu - CR-02)**: The system must provide a mechanism during an active match to abandon the game and return the user cleanly to the main menu screen (`Inicio.fxml`), resetting active match state without terminating the application process.
- **AC-16 (relates to FR-16)**: Given an active match view, when the "Salir al Menú" action is triggered, then `ControladorPrincipal` resets the match state and navigates back to `Inicio.fxml`. In addition, the repository build mechanism must synchronize all view assets (`.fxml` and `.css`) to the runtime directory (`EBOGE/bin/Vista/`) so that all interface components render properly.

## Verification Evidence Reviewed
1. `results/verification-report.md`:
   - Repository contract check (`python scripts/check_repo_contract.py`): PASS.
   - Java compilation (`javac -d EBOGE/bin [all .java]`): PASS (0 errors across `modelo`, `controlador`, `vista`).
   - Automated unit test runner (`RunRestartTests` via JUnit 5 Platform Launcher): PASS (9 tests found, 9 started, 9 successful, 0 failures, 723 ms).
   - View asset synchronization (`scripts/verify_repo.ps1` lines 69-84): PASS.
2. `results/coder-report.md`:
   - Implementation plan, self-check checklist, and stated limitations inspected.
3. Code and Test Inspection:
   - `EBOGE/src/controlador/ControladorPrincipal.java`
   - `EBOGE/src/controlador/ControladorVentanaDePartida.java`
   - `EBOGE/src/controlador/ControladorMazosYTurnos.java`
   - `EBOGE/src/vista/VentanaDePartida.fxml`
   - `EBOGE/src/vista/Partida.css`
   - `EBOGE/test/controlador/SalirPartidaTest.java`
   - `EBOGE/test/controlador/ReiniciarPartidaTest.java`
   - `scripts/verify_repo.ps1`
   - `results/pr.diff` and `results/commits.txt`

---

## Findings

### RF-01 — MUST FIX
- **Category**: Architecture Boundary Violation / Repository Integrity
- **Requirement / AC**: `ARCHITECTURE.md` (Protected Paths: `EBOGE/bin/`), `results/review-contract.md` (Protected boundaries)
- **File / Line**: Commit `0c26716` (`EBOGE/bin/*`)
- **Evidence**:
  In commit `0c26716`, 33 binary build and resource files were tracked and committed directly into git:
  ```
  EBOGE/bin/Controlador/ControladorPrincipal.class             | Bin 7859 -> 7973 bytes
  EBOGE/bin/Controlador/ControladorVentanaDePartida.class       | Bin 7556 -> 8275 bytes
  EBOGE/bin/Vista/Partida.css                                  | 101 +++++++++++++++++++
  EBOGE/bin/Vista/VentanaDePartida.fxml                        |  52 +++++++++-
  EBOGE/bin/recursos/archivos/curiosidades.txt                 |  18 ++++
  EBOGE/bin/recursos/fuentes/Font Awesome 7 Free-Solid-900.otf | Bin 0 -> 410592 bytes
  EBOGE/bin/recursos/fuentes/PressStart2P-Regular.ttf           | Bin 0 -> 116008 bytes
  EBOGE/bin/recursos/imagenes/... (16 PNG images)               | Bin 0 -> ~25MB
  EBOGE/bin/recursos/musica/... (5 MP3 audio tracks)            | Bin 0 -> ~16MB
  ```
- **Problem**:
  `ARCHITECTURE.md` explicitly designates `EBOGE/bin/` as a protected directory:
  > *`EBOGE/bin/`: Compiled output. Never edit manually.*
  
  And `results/review-contract.md` states:
  > *Runtime binaries under `EBOGE/bin/` must not be manually edited; they must be generated/synchronized by `scripts/verify_repo.ps1`.*
  
  Because the coder agent ran `git add .` after executing `verify_repo.ps1`, over 45 MB of compiled `.class` files and copied media assets (`.mp3`, `.png`, `.otf`, `.ttf`) were committed to git. Build output must be strictly transient and ignored by VCS.
- **Risk**:
  Severe repository bloat, binary merge conflicts, dirty working directory on clean builds, and clear violation of the documented architecture boundary.
- **Recommended next step**:
  Remove `EBOGE/bin/` from the git index via `git rm -r --cached EBOGE/bin/`, ensure the root `.gitignore` ignores `EBOGE/bin/`, and create a cleanup commit so only source files and verification scripts remain tracked.

---

### RF-02 — SHOULD FIX
- **Category**: Test Quality / False Confidence
- **Requirement / AC**: FR-16 / AC-16 (Match State Reset)
- **File / Line**: `EBOGE/test/controlador/SalirPartidaTest.java`: lines 57–68, 73–85
- **Evidence**:
  In `testControladorPrincipalSalirAlMenuPrincipal`:
  ```java
  57: ControladorPrincipal cp = new ControladorPrincipal();
  58: cp.configurarPartida(jugadores, 6);
  59: 
  60: assertEquals(jugadores, cp.getJugadoresPartida(), "Los jugadores deben estar configurados inicialmente");
  61: assertEquals(6, cp.getCarasDadoPartida(), "Las caras del dado deben estar configuradas");
  62: 
  63: cp.salirAlMenuPrincipal();
  64: 
  65: assertNull(cp.getPartida(), "La partida activa debe ser nula tras salir al menú principal");
  ```
  And in `testControladorVentanaDePartidaDelegaSalida`:
  ```java
  74: ControladorPrincipal cp = new ControladorPrincipal();
  75: cp.configurarPartida(jugadores, 8);
  ...
  80: cvp.salirAlMenuPrincipal();
  81: 
  82: assertNull(cp.getPartida(), "El ControladorPrincipal debe recibir la orden de salir y limpiar partida");
  ```
- **Problem**:
  In `SalirPartidaTest.java`, line 51 instantiates `partida = new Partida(jugadores, 6, mapa);` in `setUp()`, but this reference is **never assigned to `cp`**. Calling `cp.configurarPartida(jugadores, 6)` only initializes `jugadoresPartida` and `carasDadoPartida`; `cp.getPartida()` is already `null` before `salirAlMenuPrincipal()` is executed. Consequently, `assertNull(cp.getPartida())` passes trivially on an already-null field. The test never proves that an active, non-null `Partida` instance is reset.
- **Risk**:
  False confidence: A future change could delete `this.partida = null;` in `ControladorPrincipal.java`, and the entire test suite would still pass 100% green.
- **Recommended next step**:
  Introduce a package-private setter or testing seam in `ControladorPrincipal` (e.g. `void setPartidaParaPruebas(Partida partida)`) to inject an active match instance during tests, assert `assertNotNull(cp.getPartida())` prior to exit, and assert `assertNull(cp.getPartida())` afterwards.

---

### RF-03 — SHOULD FIX
- **Category**: Functional Inconsistency / Process Lifecycle
- **Requirement / AC**: FR-16 ("resetting active match state without terminating the application process")
- **File / Line**: `EBOGE/src/controlador/ControladorMazosYTurnos.java`: lines 198–200, 228–231
- **Evidence**:
  ```java
  198: ButtonType btnReiniciar = new ButtonType("Reiniciar", ButtonBar.ButtonData.OK_DONE);
  199: ButtonType btnSalir = new ButtonType("Salir", ButtonBar.ButtonData.CANCEL_CLOSE);
  200: alerta.getButtonTypes().setAll(btnReiniciar, btnSalir);
  ...
  228: } else if (respuesta.isPresent() && respuesta.get() == btnSalir) {
  229:     Platform.exit();
  230:     System.exit(0);
  231: }
  ```
- **Problem**:
  CR-02 added `btnSalirMenuOverlay` to `overlayVictoria` in `VentanaDePartida.fxml`. However, when a victory occurs, `ControladorMazosYTurnos.manejarVictoria()` immediately displays a modal dialog `alerta.showAndWait()`. If the user clicks "Salir" on this alert, line 230 calls `System.exit(0)`, terminating the JVM process. This contradicts FR-16, which requires returning cleanly to the main menu without terminating the application process. Furthermore, the modal dialog blocks interaction with `btnSalirMenuOverlay` until dismissed.
- **Risk**:
  Inconsistent user experience: Exiting from the top-bar button during the match returns cleanly to `Inicio.fxml`, but clicking "Salir" on the victory dialog abruptly shuts down the application process.
- **Recommended next step**:
  Refactor `ControladorMazosYTurnos.mostrarDialogoVictoria()` so that clicking "Salir" invokes `controladorPrincipal.salirAlMenuPrincipal()` instead of `System.exit(0)`.

---

### RF-04 — SHOULD FIX
- **Category**: PR Scope / Review Traceability
- **Requirement / AC**: ADA-09 Section 13 (PR Candidate Preparation)
- **File / Line**: `results/pr.diff` (lines 1–4680) and `results/commits.txt`
- **Evidence**:
  `results/commits.txt` lists 8 commits dating back to ADA-08 (`7612404`, `c49efd6`, `648c540`, `e932c11`, `1ee1a41`, `88fdaa7`). `results/pr.diff` is 223,420 bytes and contains files unrelated to CR-02 (e.g. `.github/workflows/verify.yml`, `.project`, `readiness-improvement-report.md`).
- **Problem**:
  The local branch `main` was left at commit `7958e09` rather than the branch base `cc71952`. Running `git diff main...HEAD > results/pr.diff` produced a massive diff containing all historical ADA-08 improvements rather than scoping strictly to CR-02.
- **Risk**:
  Obscures the true scope of CR-02 in the PR candidate artifact, causing reviewer noise and compromising auditability.
- **Recommended next step**:
  Update `main` to point to `cc71952` and regenerate `results/pr.diff` and `results/commits.txt` so they strictly contain the diff between `cc71952` and `HEAD`.

---

### RF-05 — OPTIONAL
- **Category**: User Experience (UX) / Defensive Design
- **Requirement / AC**: FR-16 (Match Exit to Main Menu)
- **File / Line**: `EBOGE/src/controlador/ControladorVentanaDePartida.java`: lines 164–168
- **Evidence**:
  ```java
  164: public void salirAlMenuPrincipal() {
  165:     if (controladorPrincipal != null) {
  166:         controladorPrincipal.salirAlMenuPrincipal();
  167:     }
  168: }
  ```
- **Problem**:
  The top-bar button `btnSalirMenu` triggers an instantaneous exit and destruction of match state with a single click. There is no confirmation dialog ("¿Estás seguro de que deseas abandonar la partida? Se perderá el progreso."). An accidental click during active gameplay permanently wipes the match.
- **Risk**:
  Accidental abandonment and frustrated player experience. (Noted by the coder agent in `coder-report.md` as a Known Limitation).
- **Recommended next step**:
  Display an `Alert(AlertType.CONFIRMATION)` prior to delegating to `controladorPrincipal.salirAlMenuPrincipal()`, proceeding only when the user confirms.

---

## Missing Evidence / Open Questions
1. **Interactive Visual Validation**:
   Automated unit tests run headless. While asset synchronization copies `.fxml` and `.css` into `EBOGE/bin/Vista/`, a human must visually launch the GUI to verify that the button styling (`.btn-salir-menu`, `.btn-salir-grande`), pixel font, and responsive layout render cleanly at runtime.
2. **Headless FXML Loading Verification**:
   In `ControladorPrincipal.java`, line 54 guards `mostrarVentanaDeInicio()` with `if (ventanaPrincipal == null) return;`. While this prevents crashes during headless tests, it also means the actual parsing and loading of `Inicio.fxml` is never executed during automated testing.

---

## Summary
- **MUST FIX**: 1 (RF-01: Committed binary build artifacts in `EBOGE/bin/` violating architectural boundaries)
- **SHOULD FIX**: 3 (RF-02: False confidence in `SalirPartidaTest`; RF-03: `System.exit(0)` in victory dialog defeating process preservation; RF-04: Inflated `pr.diff` spanning ADA-08 commits)
- **OPTIONAL**: 1 (RF-05: Missing exit confirmation dialog)

## Reviewer Recommendation
- [ ] NOT READY
- [x] READY AFTER MUST FIX
- [ ] READY FOR HUMAN REVIEW

*This recommendation is not a merge approval.*

