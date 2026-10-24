# Readiness Improvement Report

## Matriz Comparativa de Indicadores

| Indicador | BEFORE | AFTER |
| :--- | :--- | :--- |
| 1. Clarification questions | 0 (Asumió incorrectamente) | 0 (Reglas claras en AGENTS.md) |
| 2. Failed / unknown commands | Múltiples errores con javac/PowerShell | 0 (Uso exitoso de verify_repo.ps1) |
| 3. Incorrect assumptions | Uso de rutas absolutas locales | Respeto total de rutas relativas |
| 4. Out-of-scope files modified | Creación de scripts temporales de build | Ninguno |
| 5. Validations executed correctly | No (Dependencia de Eclipse) | Sí (Script automatizado) |
| 6. Final result | PARTIAL | PASS |
| 7. Qualitative observation | Alta fricción por herramientas locales | Entorno predecible y determinista |
| 8. Discoverability | Navegación a ciegas | Navegación guiada por REPO_MAP.md |
| 9. Implicit Knowledge | IK-01 expuesto | IK-01 e IK-02 documentados y mitigados |
| 10. Clasificación de mejoras | N/A | I-01 a I-06 aplicadas |
| 11. CI / shared checks | Inexistente | verify.yml unifica agente, humano y CI |

## Clasificación de Mejoras Implementadas

### Improvement I-01 (Context)
- **Artefacto:** `AGENTS.md`
- **Impacto:** Centraliza las reglas operativas y define condiciones de parada.

### Improvement I-02 (Navigation)
- **Artefacto:** `docs/REPO_MAP.md`
- **Impacto:** Elimina la exploración a ciegas delimitando el MVC.

### Improvement I-03 (Executable Knowledge)
- **Artefacto:** `scripts/verify_repo.ps1`
- **Impacto:** Codifica el conocimiento de compilación de JavaFX/JUnit en un comando universal.

### Improvement I-04 (Guardrail)
- **Artefacto:** `scripts/check_repo_contract.py`
- **Impacto:** Valida automáticamente que no falten archivos del contrato.

### Improvement I-05 (Test)
- **Artefacto:** `ReiniciarPartidaTest.java`
- **Impacto:** Automatiza la verificación de la lógica de estado sin levantar la GUI.

### Improvement I-06 (CI Check)
- **Artefacto:** `.github/workflows/verify.yml`
- **Impacto:** Asegura que el mismo script de validación corra en el servidor remoto.