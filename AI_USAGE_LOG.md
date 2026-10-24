# AI Usage Log

| Fecha | Herramienta | Tarea / Decisión | Resultado y Puntos Cualitativos |
| :--- | :--- | :--- | :--- |
| 2026-10-24 | Gemini Advanced | Auditoría inicial y detección de fricciones. | **Auditoría:** Detectamos que la falta de herramientas CLI (IK-01) era el mayor bloqueador. El agente validó que el MVC estaba bien estructurado, pero sin comandos de build, el código era intra-testable. |
| 2026-10-24 | Gemini Advanced | Diseño de `AGENTS.md` y `REPO_MAP.md`. | **Diseño de reglas:** Se aceptó la sugerencia de prohibir rutas absolutas. Se rechazó sugerir comandos de Maven/Gradle ya que el proyecto usa Java puro. |
| 2026-10-24 | Gemini Advanced | Creación de barreras automatizadas. | **Guardrail:** Se convirtió la regla "no borres documentación" en el script `check_repo_contract.py` para forzar un fallo duro si el agente elimina contexto vital. |
| 2026-10-24 | Antigravity | Ejecución AFTER de CR-01. | **Ejecución AFTER:** El comportamiento del agente cambió drásticamente; dejó de inventar comandos y ejecutó directamente `./scripts/verify_repo.ps1`. |
| 2026-10-24 | Gemini Advanced | Análisis de Discoverability y CI. | **Discoverability / CI / Limitaciones:** El conocimiento tribal de cómo configurar el classpath se hizo explícito en el script. Este check ahora se comparte en `.github/workflows/verify.yml`. La validación visual de la interfaz gráfica sigue requiriendo intervención humana. |