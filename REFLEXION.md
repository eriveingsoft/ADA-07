# Reflexión - ADA-07

**1. ¿Qué parte del workflow pertenece al juicio del LLM y cuál debe ser determinista?**
El juicio del LLM se encarga de las tareas heurísticas y analíticas: interpretar el contexto, encontrar errores lógicos en el código (como el `int x = 10;`), evaluar la arquitectura y redactar la justificación de los hallazgos. Lo determinista recae en el formato estructurado del reporte final (garantizado por el script en Python), las condiciones para activar el workflow (routing) y la estricta prohibición de ejecutar acciones de escritura (boundaries).

**2. ¿Qué información moviste de SKILL.md a references/ y por qué?**
Se movieron recursos como el checklist de revisión (`review_checklist.md`), la guía de severidad (`severity_guide.md`) y la plantilla de salida (`review_report_template.md`). Esto se hizo aplicando el principio de *progressive disclosure*, para evitar un prompt monolítico, mantener el `SKILL.md` enfocado únicamente en la orquestación y permitir que los recursos se modifiquen de forma independiente en el futuro.

**3. ¿Qué función cumple la description del frontmatter en el routing?**
Actúa como el mecanismo de activación (trigger). El orquestador de agentes (como Antigravity) lee esta descripción para entender bajo qué contexto, intenciones o palabras clave (ej. "review PR", "check diff") debe enrutar la petición del usuario hacia esta Skill específica en lugar de usar su comportamiento por defecto.

**4. ¿Qué prompt negativo fue más difícil de evitar que activara la Skill?**
Los prompts ambiguos como "Revisa el código y arregla el error". La palabra "revisa" puede causar confusión semántica en el agente, haciéndolo dudar entre iniciar un proceso de "code review" formal (activando la Skill) o simplemente actuar como un asistente de codificación normal para editar el archivo.

**5. ¿Qué diferencia práctica observaste entre una Skill y un MCP Server?**
Un MCP Server proporciona las *capacidades* o herramientas deterministas (por ejemplo, poder leer un archivo local o comunicarse con la API de GitHub). Una Skill, en cambio, define el *flujo de trabajo* (workflow); es decir, le dice al agente paso a paso cómo y cuándo usar esas herramientas del MCP para lograr un objetivo específico siguiendo las reglas de la ingeniería de software.

**6. ¿Qué aporta el script que no conviene resolver con instrucciones de lenguaje natural?**
El script de validación (`validate_review_report.py`) aporta garantías absolutas. El lenguaje natural es probabilístico y el LLM puede "olvidar" incluir un campo obligatorio (como la decisión humana). El script actúa como una compuerta de calidad (quality gate) que falla de forma determinista (exit code 1) si la estructura no es exactamente la requerida.

**7. ¿Qué riesgo existiría si esta Skill pudiera comentar o hacer merge en GitHub?**
El riesgo de una automatización irresponsable sin supervisión. El agente podría aprobar e integrar código con errores graves (como fallos de compilación), introducir vulnerabilidades o romper la rama principal del proyecto, violando el principio de "Human in the loop".

**8. ¿Qué cambió entre la primera y la última versión de tu Skill?**
La iteración principal consistió en hacer más estricta la plantilla de salida (`assets/review_report_template.md`) para forzar explícitamente el campo "Human decision required: YES" al final del documento. Además, se refinaron las instrucciones de "Boundaries" para asegurar que la Skill operara de forma estrictamente "Read-only".

**9. ¿Qué evidencia te permite afirmar que la Skill es reutilizable?**
El diseño separa claramente la lógica del negocio. La Skill base opera de forma agnóstica a un lenguaje de programación específico evaluando los diffs y especificaciones proporcionadas, y como las reglas están desacopladas en la carpeta `references/`, se puede usar en cualquier otro repositorio simplemente cambiando el checklist sin tocar el workflow principal.

**10. ¿Qué otro proceso profesional de Ingeniería de Software convertirías en una Skill?**
La "Traducción de Requerimientos a Casos de Prueba Unitarios (TDD)". Podría ser una Skill que analice un caso de uso (como las transiciones de las casillas en EBOGE) y genere un esqueleto de pruebas en JUnit 5 utilizando técnicas de partición de clases de equivalencia y análisis de valores límite antes de que el desarrollador empiece a programar.

**11. ¿Qué diferencia de diseño fue más evidente entre tu Skill y code-review-and-quality?**
La arquitectura de la revisión. Mientras mi Skill seguía un flujo lineal general basado en un checklist, la Skill profesional está estructurada como un análisis "multieje", evaluando proactivamente y por separado la correctitud funcional, la legibilidad, la arquitectura (identificó el patrón Strategy sola), la seguridad y el rendimiento.

**12. ¿Qué ventaja y qué costo observaste al usar una Skill mucho más extensa y opinionated?**
La ventaja es que el análisis es sumamente profundo y capaz de diagnosticar la causa raíz exacta de errores de compilación o sugerir patrones de diseño. El costo es el "ruido"; al ser muy *opinionated*, se queja de detalles minúsculos de estilo (como el uso de tabuladores en lugar de 4 espacios), lo cual puede desviar la atención de los problemas críticos de lógica.

**13. ¿Qué controles aplicarías antes de adoptar una Skill de terceros en un repositorio empresarial?**
Una auditoría de cadena de suministro (*Supply-chain review*): revisar el código fuente de la Skill, inspeccionar qué herramientas MCP requiere (validando que sean de solo lectura) y asegurarse de que no requiera tokens ni llaves de API que puedan comprometer o exfiltrar información propietaria del repositorio.

**14. Después de estudiar la Skill profesional, ¿qué cambiarías en una versión 2 de tu Skill y cómo evaluarías que realmente mejoró?**
Adoptaría la categorización por ejes (Correctness, Architecture, Security, Tests) y mejoraría la granularidad del sistema de severidad (introduciendo "Critical" para problemas que rompen la compilación). Lo evaluaría pasando un conjunto de PRs de prueba con vulnerabilidades inyectadas y midiendo si la nueva versión produce hallazgos más precisos y accionables en la tabla final sin alterar el formato validado por el script.