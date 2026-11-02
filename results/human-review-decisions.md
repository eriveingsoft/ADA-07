# Human Review Decisions

## Change
CR-02 (Match Exit to Main Menu & Resource Sync)

### RF-01
- **Reviewer severity**: MUST FIX
- **Human decision**: ACCEPTED
- **Reason**: The finding is completely justified. `ARCHITECTURE.md` strictly defines `EBOGE/bin/` as a protected compiled directory that must never be committed. Committing 45 MB of compiled classes and media assets causes severe repo bloat and breaks the repository contract.
- **Required action**: Add `.gitignore` to ignore `EBOGE/bin/`, execute `git rm -r --cached EBOGE/bin/`, and ensure build artifacts remain transient.

### RF-02
- **Reviewer severity**: SHOULD FIX
- **Human decision**: ACCEPTED
- **Reason**: The reviewer accurately identified false confidence in `SalirPartidaTest.java`. The test instantiated `Partida` but never assigned it to `ControladorPrincipal`, causing `assertNull(cp.getPartida())` to pass vacuously on an already-null field.
- **Required action**: Add a test setter `setPartida(Partida partida)` in `ControladorPrincipal` and update `SalirPartidaTest.java` to assert `assertNotNull(cp.getPartida())` before exit, and `assertNull(cp.getPartida())` after exit.

### RF-03
- **Reviewer severity**: SHOULD FIX
- **Human decision**: ACCEPTED
- **Reason**: Calling `System.exit(0)` on the victory alert in `ControladorMazosYTurnos.java` contradicts requirement `FR-16`, which explicitly states that the game must reset without terminating the application process.
- **Required action**: Refactor `ControladorMazosYTurnos.java` to invoke `controladorPrincipal.salirAlMenuPrincipal()` when the user clicks "Salir" on the victory dialog instead of terminating the JVM.

### RF-04
- **Reviewer severity**: SHOULD FIX
- **Human decision**: ACCEPTED
- **Reason**: `results/pr.diff` contains 8 commits from ADA-08 because local `main` was not aligned with the base commit `cc71952`.
- **Required action**: Align local `main` to `cc71952` and regenerate `results/pr.diff` and `results/commits.txt` so that PR evidence reflects only CR-02 changes.

### RF-05
- **Reviewer severity**: OPTIONAL
- **Human decision**: DEFERRED
- **Reason**: While adding an exit confirmation dialog is a good UX practice, it is outside the minimum acceptance criteria for CR-02 and adds complexity to automated headless testing.
- **Required action**: Record as deferred technical debt for a future release.

## Human Gate
- [x] Return to coder-agent
- [ ] Ready for final re-review
- [ ] Stop / escalate

