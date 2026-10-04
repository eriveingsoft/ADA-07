# Professional Skill Anatomy: code-review-and-quality

**Skill analizada:** `code-review-and-quality` (addyosmani/agent-skills)

| Elemento | Identificación en el diseño de la Skill |
| :--- | :--- |
| **Routing** | La descripción (`description`) incluye frases clave muy específicas (como "review diff", "pull request", "assess code quality") que le indican al agente exactamente cuándo debe enrutar la petición hacia esta Skill. |
| **Workflow** | Define un proceso secuencial estructurado: primero lee el contexto/especificaciones, luego inspecciona el diff completo, aplica reglas de revisión y finalmente pasa por "quality gates" (como asegurar que no haya violaciones de seguridad críticas) antes de emitir un veredicto final. |
| **Review axes** | La Skill no solo busca errores de sintaxis, sino que divide su análisis explícitamente en: Correctness (funcionalidad), readability/simplicity (código limpio), architecture (diseño de software), security (vulnerabilidades) y performance (eficiencia). |
| **Severity** | Clasifica los hallazgos en niveles estrictos. Diferencia claramente los "blockers" (Must Fix / Required) que impiden el merge, de las sugerencias opcionales (Should Fix / Nitpicks) que son solo feedback para mejorar. |
| **Verification** | Exige evidencia comprobable antes de cerrar la revisión. No asume que el código funciona solo con leerlo; pide revisar los resultados de las pruebas (tests) o la salida de validaciones previas. |
| **Composition** | Está diseñada para colaborar con otras herramientas. Si detecta problemas complejos, recomienda derivar el análisis a Skills más especializadas de la misma colección, como `security-and-hardening` o `performance-optimization`. |
| **Boundaries** | Establece límites estrictos de "solo lectura". Deja la decisión de aprobación al humano y prohíbe explícitamente ejecutar acciones silenciosas como modificar el código fuente, escribir comentarios en GitHub, o hacer merge del PR por su cuenta. |