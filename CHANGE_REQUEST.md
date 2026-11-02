# Change Request

Change ID: CR-02

## Goal
Implement a match exit mechanism allowing users to abandon an active game session and return cleanly to the main menu screen (`Inicio.fxml`), clearing active match state in `ControladorPrincipal`. Additionally, enhance the build script `scripts/verify_repo.ps1` to automatically synchronize all view resources (`.fxml` and `.css`) from `src/vista/` into `EBOGE/bin/Vista/`, resolving the defect where UI buttons defined in FXML fail to render at runtime.

## Requirements Covered
- FR-16 (Match Exit to Main Menu)
- AC-16 (relates to FR-16)

## Constraints
- Follow AGENTS.md.
- Use REQUIREMENTS.md and SPEC.md as the source of truth.
- Preserve MVC architecture: navigation and coordination belong to `src/controlador/`, view definitions to `src/vista/`, domain state to `src/modelo/`.
- Do not weaken or remove existing tests (`ReiniciarPartidaTest` must continue passing).

## Verification
- Automated unit test suite `EBOGE/test/controlador/SalirPartidaTest.java` verifying that:
  1. Triggering `salirAlMenuPrincipal()` clears the active `partida`, `jugadoresPartida`, and `carasDadoPartida` in `ControladorPrincipal`.
  2. The controller references propagate correctly without crashing.
- Build script `scripts/verify_repo.ps1` copies all `.fxml` and `.css` files into `EBOGE/bin/Vista/`.
- `./scripts/verify_repo.ps1` executes all tests (existing and new) and passes with exit code 0 (`VERIFY REPO: PASS`).

## Human Approval
Approved to implement: YES
Approved by: Erick Vega (Human Reviewer)
Date: 2026-11-02
