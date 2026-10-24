# Repository Readiness Audit

## BEFORE

| Dimension | Score 0-5 | Weighted | Evidence | Main Gap |
| :--- | :--- | :--- | :--- | :--- |
| Entry Point & Navigation / 15 | 2 | 6 | Hay un README general, pero no existe un mapa de arquitectura ni rutas claras. | Falta un REPO_MAP.md. |
| Agent Instructions / 15 | 0 | 0 | No existe un archivo AGENTS.md. | Faltan reglas de cambio y comandos estándar. |
| Build / Run / Test / 15 | 1 | 3 | README instruye descargar un .jar. Pruebas unitarias requieren Eclipse. | Faltan comandos de terminal para compilar y probar. |
| Architecture Boundaries / 10 | 3 | 6 | Patrón MVC aplicado en carpetas (src/modelo, src/vista, src/controlador). | Faltan reglas explícitas de dependencias. |
| Requirements & Traceability / 10 | 2 | 4 | Existe ERS_EBOGE.md con IDs (RF-01), pero el diagrama es PDF y no hay tareas. | Falta trazabilidad de IDs hacia los tests/código. |
| Automation & Guardrails / 10 | 0 | 0 | Ausencia total de validaciones automáticas, linting o hooks. | Falta un script de verificación (verify_repo.sh). |
| Testability & Evidence / 15 | 1 | 3 | Pruebas acopladas al IDE. Definition of Done ambiguo. | Falta evidencia reproducible desde terminal. |
| Safety & Permissions / 10 | 1 | 2 | No hay límites explícitos documentados para el agente. | Faltan rutas protegidas (protected-paths). |

**TOTAL BEFORE: 24/100**

Top 3 friction points:
1. Imposibilidad de correr pruebas y compilar desde la terminal (dependencia de Eclipse).
2. Falta de instrucciones operativas explícitas (`AGENTS.md`).
3. Falta de un mapa del repositorio inteligible para el modelo (`REPO_MAP.md`).