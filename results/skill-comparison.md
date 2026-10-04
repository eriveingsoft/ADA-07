# Skill Comparison - ADA-07

## Comparación de Dimensiones

| Dimensión | Mi Skill (reviewing_pull_requests) | Profesional (code-review-and-quality) | Conclusión |
| :--- | :--- | :--- | :--- |
| **Trigger/routing** | Uso de frases simples en `description` del `SKILL.md`. | Integración nativa con `using-agent-skills` y triggers altamente específicos. | La profesional tiene un ecosistema de orquestación más robusto. |
| **Scope/boundaries** | Read-only. Genera reporte sin modificar GitHub. | Read-only. Tampoco aprueba ni hace merge, dejando la autoridad al humano. | Ambas respetan correctamente el límite de no escribir código ni alterar repositorios directamente. |
| **Workflow depth** | Secuencial básico: lee spec, revisa diff, aplica checklist y clasifica. | Complejo y estructurado por "Quality Gates" evaluando 5 ejes distintos. | La profesional es mucho más exhaustiva y procedimental. |
| **Correctness** | Detecta el error de compilación como MUST FIX. | Detecta el error como "Critical" (🔴 Falló). | Ambas son eficaces para atrapar errores obvios de sintaxis. |
| **Architecture** | Solo verifica si se respeta `ARCHITECTURE.md` (si existe). | Evalúa proactivamente patrones de diseño (ej. reconoció el patrón Strategy). | La profesional infiere arquitectura incluso sin un `.md` que se lo indique. |
| **Security** | Revisa estáticamente credenciales o validaciones. | Analiza vectores de vulnerabilidad implícitos. | Nivel de escrutinio similar, pero la profesional parece usar un modelo de amenazas más amplio. |
| **Performance** | No se evaluó de forma explícita. | Valida regresiones de complejidad computacional y sobrecostos. | La profesional incluye rendimiento por defecto como quality gate. |
| **Tests/verification** | Exige evidencia automatizada y cobertura de edge cases. | Falló la revisión por ausencia de suite unitaria aislada en JUnit 5. | Ambas priorizan fuertemente la existencia de pruebas unitarias. |
| **Severity model** | MUST FIX, SHOULD FIX, OPTIONAL. | Critical, Required, Nit, Consider. | Nomenclatura distinta pero lógica idéntica (blockers vs feedback opcional). |
| **Human review** | Requiere una variable final indicando "Human decision required: YES". | Detiene el proceso y presenta el reporte estructurado para aprobación. | Ambas mantienen la filosofía de "Human in the loop". |
| **References/composition** | Usa archivos locales `review_checklist.md` y `severity_guide.md`. | Puede mandar a llamar otras Skills de su ecosistema (ej. seguridad o performance). | La profesional es modular e interoperable. |
| **Reusability** | Alta para flujos básicos de PR. | Extrema, agnóstica al lenguaje y adaptable a cualquier proyecto. | La profesional está diseñada para un uso agnóstico industrial. |

## Reflexiones

**¿Qué hace la Skill profesional que tu Skill no contempló?**
Realiza una revisión multieje explícita (Correctness, Readability, Architecture, Security, Performance) independientemente de si se lo pides o no. Además, evalúa estilo y convenciones (como el uso de tabuladores vs espacios) e identifica patrones de diseño por su cuenta.

**¿Qué elementos de tu Skill son más específicos al curso/proyecto y conviene conservar?**
La validación determinista estricta mediante el script en Python (`validate_review_report.py`) que obliga al agente a mantener un formato idéntico en el output, y la integración directa con los requerimientos (RF/RNF) específicos del proyecto EBOGE.

**¿Qué agregarías a una versión 2 de tu Skill?**
1. Categorizar el análisis por ejes explícitos (seguridad, rendimiento, etc.).
2. Mejorar el modelo de severidad para incluir "Critical" (rompe compilación) vs "Required" (rompe requerimiento).

**¿Qué NO copiarías y por qué?**
No copiaría el nivel de opinión estricta sobre estilos (como quejarse de un tabulador), ya que en un entorno universitario o de prototipado rápido puede generar demasiado "ruido" en revisiones que deberían centrarse únicamente en lógica de negocio.

**¿Cuál produjo hallazgos más accionables? Incluye dos ejemplos.**
La profesional fue ligeramente más accionable por su nivel de detalle. Ejemplos:
1. En lugar de decir "Falta prueba", especificó "Crear la clase de prueba unitaria con JUnit 5 validando las transiciones de estado".
2. No solo indicó que había un error de sintaxis, sino que diagnosticó el error exacto del compilador ("implicitly declared classes are a preview feature...").