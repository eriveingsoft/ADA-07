# Third-Party Skill Pre-Install Audit

**Repository:** https://github.com/addyosmani/agent-skills
**Selected skill:** code-review-and-quality
**Version / commit inspected:** main (latest)

## Purpose
Realizar revisiones de código profesionales evaluando corrección, legibilidad/simplicidad, arquitectura, seguridad y rendimiento.

## Trigger / Description
Se activa cuando se solicita revisar código, un diff o un PR, y categoriza los hallazgos con base en "quality gates" predefinidos.

## Supporting References
Utiliza referencias y conocimiento de otras áreas, y puede recomendar el uso de otras skills complementarias como `security-and-hardening` o `performance-optimization`.

## Tools / Commands / Permissions
Requiere acceso de lectura al código fuente. No ejecuta comandos de escritura, aprobación ni operaciones de merge por defecto.

## Potential Risks
Al ser una Skill muy extensa y "opinionated", podría sugerir estándares arquitectónicos que sobrepasen las necesidades de un proyecto universitario. Sin embargo, no representa un riesgo de seguridad directo al ser de alcance "Draft-Only".

## Decision
[x] Install
[ ] Do not install

**Reason:**
La Skill proviene de un repositorio reconocido, no requiere permisos destructivos (escritura/merge), y es un requisito académico obligatorio para la Parte II de la práctica.