# Workflow Observation

## Trigger
¿Cuándo debería utilizarse este workflow?
Cuando se solicite revisar los cambios de código locales de una rama o un Pull Request para evaluar si está listo para una revisión humana.

## Inputs
¿Qué necesita para iniciar?
- El código modificado (diff local o PR del repositorio).
- Archivos de contexto del proyecto: `ERS_EBOGE.md`, `Pruebas de aceptación.md` y `Estándar de Programación.md`.
- Resultados de la ejecución de las pruebas.

## Steps Performed
1. Leer `ERS_EBOGE.md` y `Pruebas de aceptación.md` para entender el alcance y las reglas de los cambios esperados.
2. Leer el `Estándar de Programación.md` para conocer las convenciones del equipo.
3. Inspeccionar los archivos modificados en el diff.
4. Identificar si existe evidencia de pruebas para el nuevo comportamiento.
5. Evaluar el código contra el checklist de revisión de la Skill.
6. Clasificar los hallazgos encontrados (MUST FIX, SHOULD FIX, OPTIONAL).
7. Generar el reporte de revisión estructurado.

## Decisions
¿Qué decisiones requieren criterio del LLM?
- Juzgar si los cambios en el código cumplen realmente con lo estipulado en el `ERS_EBOGE.md` y las `Pruebas de aceptación.md`.
- Decidir la gravedad de un error de código o de diseño para clasificarlo correctamente.
- Evaluar la claridad y calidad del código (si respeta el estándar del equipo).

## Deterministic Work
¿Qué pasos deberían ser scripts/comandos?
- La ejecución de las pruebas del proyecto.
- La validación del formato del reporte final (verificar estructuralmente que no le falten secciones).

## Reference Knowledge
¿Qué checklist, criterios o convenciones se consultaron?
- Guía de severidad para clasificar errores (`references/severity_guide.md`).
- Checklist de revisión de código (`references/review_checklist.md`).

## Output
¿Qué artefacto produce?
Un archivo Markdown estructurado con los resultados de la revisión en `results/pr-readiness-review.md`.

## Stop Conditions
¿Cuándo debe detenerse y pedir intervención humana?
- Si los requerimientos definidos en `ERS_EBOGE.md` contradicen lo que se pide en `Pruebas de aceptación.md`.
- Si el usuario solicita modificar código, hacer comentarios directos en GitHub, aprobar el PR o hacer merge.