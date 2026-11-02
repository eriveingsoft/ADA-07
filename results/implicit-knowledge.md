# Implicit Knowledge (IK) Discovery

## IK-01: Compilación acoplada al IDE
- **Descripción:** El proyecto dependía exclusivamente de la configuración interna de Eclipse para inyectar JavaFX y JUnit al classpath. 
- **Impacto:** El agente fallaba al intentar compilar desde la terminal estándar.
- **Mitigación:** Se codificó la resolución de dependencias en `scripts/verify_repo.ps1`.

## IK-02: Límites del patrón MVC y Archivos Autogenerados
- **Descripción:** No existía una regla explícita que impidiera a un colaborador (o agente) insertar lógica de negocio en `src/vista` o intentar modificar manualmente archivos `.class` en `EBOGE/bin`.
- **Impacto:** Riesgo alto de corrupción de la arquitectura y pérdida de código compilado.
- **Mitigación:** Documentación de fronteras arquitectónicas en `ARCHITECTURE.md` y protección explícita de rutas en `docs/REPO_MAP.md` y `AGENTS.md`.