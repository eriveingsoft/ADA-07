# Coder Agent Report

## Change
Change ID: CR-02
Branch: ada09/cr-02
Base commit: cc71952

## Requirements Covered
- FR-16 (Match Exit to Main Menu)
- AC-16 (relates to FR-16)

## Implementation Plan
1. Fix resource synchronization bug in `scripts/verify_repo.ps1` so that `.fxml` and `.css` files from `src/vista/` and `recursos/` are copied to runtime output directories (`EBOGE/bin/Vista/` and `EBOGE/bin/vista/`).
2. Add the "Salir al Menú" button (`btnSalirMenu`) to `EBOGE/src/vista/VentanaDePartida.fxml` in the top bar (next to `btnReiniciar`) and `btnSalirMenuOverlay` in the victory overlay.
3. Define CSS styles `.btn-salir-menu` and `.btn-salir-grande` in `EBOGE/src/vista/Partida.css` consistent with the retro pixel visual guidelines.
4. Update `ControladorVentanaDePartida.java` to inject `@FXML private Button btnSalirMenu;` and `@FXML private Button btnSalirMenuOverlay;`, bind action handlers, and implement `salirAlMenuPrincipal()` delegating to `ControladorPrincipal`.
5. Update `ControladorPrincipal.java` to safeguard `mostrarVentanaDeInicio()` against null `ventanaPrincipal` in headless environments and implement `salirAlMenuPrincipal()`, nullifying `partida`, `jugadoresPartida`, and resetting `carasDadoPartida` to 0 before loading `Inicio.fxml`.
6. Implement comprehensive unit tests in `EBOGE/test/controlador/SalirPartidaTest.java`.
7. Configure `scripts/verify_repo.ps1` test runner to execute both `ReiniciarPartidaTest` and `SalirPartidaTest`.
8. Execute verification script to validate compilation, repository contract, and 100% test pass rate.

## Files Changed
- `scripts/verify_repo.ps1`: Added asset sync to `bin/` and included `SalirPartidaTest` in runner.
- `EBOGE/src/vista/VentanaDePartida.fxml`: Added `btnSalirMenu` and `btnSalirMenuOverlay`.
- `EBOGE/src/vista/Partida.css`: Added styling classes `.btn-salir-menu` and `.btn-salir-grande`.
- `EBOGE/src/controlador/ControladorVentanaDePartida.java`: Injected button fields, action bindings, and delegation method `salirAlMenuPrincipal()`.
- `EBOGE/src/controlador/ControladorPrincipal.java`: Added `salirAlMenuPrincipal()` method and null protection in `mostrarVentanaDeInicio()`.
- `EBOGE/test/controlador/SalirPartidaTest.java`: New unit test suite for CR-02.
- `REQUIREMENTS.md`: Added FR-16.
- `SPEC.md`: Added AC-16.
- `TASKS.md`: Registered task CR-02.
- `CHANGE_REQUEST.md`: Change Request specification for CR-02.

## Tests Added / Updated
- `controlador.SalirPartidaTest` (4 tests added):
  - `testControladorPrincipalSalirAlMenuPrincipal`: Asserts that `ControladorPrincipal` resets match reference, player list, and dice face count upon exit.
  - `testControladorVentanaDePartidaDelegaSalida`: Verifies end-to-end controller propagation.
  - `testBotonesSalirEnControladorVentanaDePartida`: Verifies getter and setter access for button components.
  - `testSalirSinControladorPrincipalNoLanzaExcepcion`: Asserts defensive null-handling when `ControladorPrincipal` is not yet attached.
- `controlador.ReiniciarPartidaTest` (5 tests preserved):
  - All 5 baseline tests pass cleanly without modifications.

## Commands Executed
- `powershell -ExecutionPolicy Bypass -File "./scripts/verify_repo.ps1"`

## Verification Result
- Repository contract: PASS
- Java source compilation (MVC): PASS
- Test compilation & execution: PASS (9 tests found, 9 tests started, 9 tests successful, 0 failures)
- Overall result: PASS

## Assumptions
- When returning to the main menu, any active match state in memory is discarded to avoid memory leaks or state bleeding into new games.
- The UI controls use standard JavaFX Button components matching the existing `btnReiniciar` convention.

## Known Limitations
- Does not yet display a secondary confirmation dialog ("¿Estás seguro de salir?") before exiting to the menu; the exit action is immediate upon clicking.

## Self-Check
[x] Scope respected
[x] Existing tests not weakened
[x] No undocumented dependency/API introduced
[x] Ready for independent review

This report is NOT an approval.
