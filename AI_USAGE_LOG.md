# AI Usage Log

| Fecha | Herramienta | Tarea / Decisión | Resultado y Puntos Cualitativos |
| :--- | :--- | :--- | :--- |
| 2026-10-24 | Gemini Advanced | Auditoría inicial y detección de fricciones. | **Auditoría:** Detectamos que la falta de herramientas CLI (IK-01) era el mayor bloqueador. El agente validó que el MVC estaba bien estructurado, pero sin comandos de build, el código era intra-testable. |
| 2026-10-24 | Gemini Advanced | Diseño de `AGENTS.md` y `REPO_MAP.md`. | **Diseño de reglas:** Se aceptó la sugerencia de prohibir rutas absolutas. Se rechazó sugerir comandos de Maven/Gradle ya que el proyecto usa Java puro. |
| 2026-10-24 | Gemini Advanced | Creación de barreras automatizadas. | **Guardrail:** Se convirtió la regla "no borres documentación" en el script `check_repo_contract.py` para forzar un fallo duro si el agente elimina contexto vital. |
| 2026-10-24 | Antigravity | Ejecución AFTER de CR-01. | **Ejecución AFTER:** El comportamiento del agente cambió drásticamente; dejó de inventar comandos y ejecutó directamente `./scripts/verify_repo.ps1`. |
| 2026-10-24 | Gemini Advanced | Análisis de Discoverability y CI. | **Discoverability / CI / Limitaciones:** El conocimiento tribal de cómo configurar el classpath se hizo explícito en el script. Este check ahora se comparte en `.github/workflows/verify.yml`. La validación visual de la interfaz gráfica sigue requiriendo intervención humana. |

## ADA-09: Agentic Code Review Log

| Fecha | Rol / Fase | Tarea / Decisión | Detalle y Razón |
| :--- | :--- | :--- | :--- |
| 2026-11-02 | Human Gate 0 | Definición y refinamiento del Change Request (CR-02). | Se acordó con el revisor humano implementar el botón de "Salir al Menú Principal" (FR-16 / AC-16) y solucionar el problema de sincronización de recursos (`.fxml` y `.css`) en `verify_repo.ps1`, descartando la validación de jugadores por ya estar contemplada en la UI. |
| 2026-11-02 | Coder Agent | Plan de implementación y diseño arquitectónico de CR-02. | Se elaboró un plan de 8 pasos priorizando: 1) arreglar la sincronización de assets a `bin/`, 2) inyectar botones en FXML/CSS, 3) lógica en controladores sin romper MVC, 4) tests unitarios desacoplados con headless fallback. |
| 2026-11-02 | Coder Agent | Implementación y verificación de CR-02. | Se codificó la funcionalidad. Se aceptó la sugerencia de proteger `mostrarVentanaDeInicio()` contra `ventanaPrincipal == null` para evitar fallos en ejecución headless de tests. Se rechazó alterar los tests existentes de `ReiniciarPartidaTest`, alcanzando 9/9 tests exitosos. Se generó `results/coder-report.md`. |
