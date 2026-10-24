# Reflexión Individual - ADA-08

**1. ¿Cuál fue el principal obstáculo para el agente en la ejecución BEFORE?**
La ausencia de un pipeline de compilación estándar desde la terminal. Como EBOGE dependía de Eclipse, el agente no sabía cómo compilar Java ni ejecutar JUnit, obligándolo a inventar rutas absolutas locales (`C:\Users\erive\...`).

**2. ¿Qué información tuvo que inferir el agente que un desarrollador experimentado ya conocía?**
Que la arquitectura seguía un patrón MVC estricto (dónde estaban las vistas vs. controladores) y cómo estaban configuradas localmente las librerías de JavaFX y JUnit.

**3. ¿Qué mejora produjo el mayor impacto y por qué?**
El script `verify_repo.ps1`. Eliminó la adivinanza del agente dándole un comando universal y determinista para validar su propio código sin depender del IDE.

**4. ¿Qué información pertenece en AGENTS.md y qué información NO debería vivir ahí?**
Sí pertenecen las reglas operativas, comandos estándar y límites arquitectónicos. NO pertenecen los requerimientos del software (como las reglas del dado o las cartas), los cuales deben vivir exclusivamente en su fuente de verdad (`ERS_EBOGE.md`).

**5. ¿Qué regla convertiste en software/hook/script en lugar de dejarla como instrucción?**
La preservación del contexto. En lugar de pedirle al agente en texto "no borres mis archivos de instrucciones", creamos `check_repo_contract.py` para que la ejecución falle inmediatamente si falta un archivo clave.

**6. ¿Qué diferencia observaste entre 'más contexto' y 'mejor contexto'?**
Más contexto sería darle al agente todos los archivos `.class` de Eclipse o código irrelevante. Mejor contexto fue crear el `REPO_MAP.md`, que funciona como un índice preciso para guiarlo directamente a la capa `src/controlador/`.

**7. ¿Qué riesgo aparece cuando CLAUDE.md/GEMINI.md/copilot instructions duplican información del proyecto?**
Se rompe el principio de Single Source of Truth (SSOT). Si los requerimientos cambian, tendrías que actualizarlos en el ERS y en las instrucciones del agente. Si olvidas uno, el agente generará código basado en reglas obsoletas.

**8. ¿Qué checks comparten ahora el agente, el desarrollador humano y CI?**
El script de PowerShell (`./scripts/verify_repo.ps1`). Funciona exactamente igual para el humano en la terminal local, para el agente en su entorno de ejecución y para un futuro pipeline automatizado.

**9. ¿Qué parte del proceso sigue requiriendo decisión humana?**
La traducción inicial de las necesidades a requerimientos formales (el diseño del ERS) y la revisión crítica para aprobar fusiones (Pull Requests) y cambios estructurales en la arquitectura.

**10. ¿Qué evidencia demuestra que el AFTER es realmente mejor?**
Los logs generados en `after-agent-run.md`, donde el agente no tuvo que hacer preguntas aclaratorias ni hardcodear rutas, logrando compilar y pasar las pruebas exitosamente utilizando la herramienta estándar del repositorio.

**11. ¿Qué mejora realizada para el agente también beneficia a un desarrollador que entra por primera vez al proyecto?**
El `REPO_MAP.md` y el script de validación. Un desarrollador nuevo ya no necesita que alguien le explique cómo configurar su IDE; solo lee el mapa y ejecuta el script para empezar a aportar valor.

**12. ¿Qué variable no controlada podría afectar tu comparación BEFORE/AFTER?**
El no determinismo de los LLMs. Incluso usando el mismo modelo, las alucinaciones al inventar comandos en el BEFORE pueden variar de una ejecución a otra, haciendo que algunas pruebas fallen de manera distinta.

**13. ¿Qué elementos de este ADA conservarás como base para tu proyecto final?**
La estructura de blindaje: el contrato operativo (`AGENTS.md`), la navegación estructural (`REPO_MAP.md`) y el pipeline de verificación universal (`verify_repo` y `check_repo_contract`).