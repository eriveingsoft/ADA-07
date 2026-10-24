# Repository Map

## Entry Points
- `EBOGE/src/vista/VentanaPrincipal.java`: Main application bootstrap and GUI setup.
- `EBOGE/src/controlador/`: Controllers coordinating views and logic.
- `EBOGE/src/modelo/`: Domain models and core business rules (Dice, Board, Players).

## Tests
- `EBOGE/test/`: JUnit test suite.

## Change Guide
If changing game logic or rules:
1. Check `Documentación/Diagramas/ERS_EBOGE.md`.
2. Update the domain layer in `src/modelo/`.
3. Modify controllers in `src/controlador/` if transition logic changes.
4. Run `./scripts/verify_repo.ps1` to validate compilation.

## Do Not Edit Directly
- `EBOGE/bin/`: Compiled `.class` files.
- `.github/workflows/`: CI configurations.