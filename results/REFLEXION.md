# Reflexión Individual - ADA-09: Agentic Code Review

## 1. ¿Qué encontró el reviewer-agent que el coder-agent no detectó?
El coder-agent se enfocó estrictamente en hacer que el nuevo botón funcionara y en que la suite de pruebas terminara en verde. Sin embargo, el reviewer-agent identificó tres problemas críticos que el coder pasó por alto:
1. **Contaminación del índice de Git (RF-01)**: El coder ejecutó `git add .` tras compilar, versionando más de 45 MB de archivos `.class`, audios `.mp3` e imágenes compiladas en `EBOGE/bin/`, violando la regla de arquitectura sobre rutas protegidas.
2. **Falsa confianza en las pruebas unitarias (RF-02)**: La aserción `assertNull(cp.getPartida())` pasaba porque el campo ya era nulo antes de invocar la acción de salida, nunca comprobó una transición real de estado.
3. **Inconsistencia de ciclo de vida (RF-03)**: El diálogo de victoria en `ControladorMazosYTurnos.java` ejecutaba `System.exit(0)`, contradiciendo la especificación de `FR-16` que exige abandonar la partida sin matar el proceso.

## 2. ¿Qué finding fue más difícil de clasificar y por qué?
El hallazgo **RF-02 (Falsa confianza en el test)**. Inicialmente podría parecer un `OPTIONAL` o una simple mejora de estilo de prueba porque los tests estaban pasando ("verde"), pero al analizarlo con el `Review Contract`, se clasificó como `SHOULD FIX` debido a que una prueba que pasa vacíamente sin validar el estado mutado anula por completo la red de seguridad del repositorio frente a futuras regresiones.

## 3. ¿Qué finding rechazaste o modificaste como humano y qué evidencia usaste?
Se modificó/difirió el hallazgo **RF-05 (Diálogo modal de confirmación antes de salir)**. Aunque el reviewer lo propuso como una buena práctica de experiencia de usuario (UX), como revisor humano decidí marcarlo como **DEFERRED**. La evidencia utilizada fue la especificación en `CHANGE_REQUEST.md` y `AC-16`: el criterio de aceptación exigía únicamente el reseteo del estado y la navegación limpia; introducir un diálogo emergente modal en este punto añadía complejidadinnecesaria y riesgos de bloqueo en las ejecuciones headless de JUnit en CI.

## 4. ¿Hubo algún test verde que produjera falsa confianza? Explica.
Sí, exactamente `testControladorPrincipalSalirAlMenuPrincipal` en `SalirPartidaTest.java`. El coder-agent instanció un objeto `partida` en el `setUp()`, pero olvidó asignarlo a la instancia del `ControladorPrincipal`. Al ejecutar `cp.salirAlMenuPrincipal()`, `cp.getPartida()` ya era `null`. La prueba arrojaba un "PASS" inmediato en verde, pero si un desarrollador eliminaba por error la línea `this.partida = null;` en producción, el test seguiría pasando en verde. El revisor detectó este falso positivo y se solucionó agregando `setPartida` y verificando `assertNotNull` antes de la llamada.

## 5. ¿Verificaste alguna API o dependencia que parecía plausible? ¿Qué encontraste?
Sí, se verificó el uso de `Platform.exit()` y `System.exit(0)` en `ControladorMazosYTurnos.java`. Aunque ambas son APIs estándar y plausibles de Java y JavaFX para cerrar aplicaciones de escritorio, violaban directamente el requerimiento de negocio (`FR-16`), que estipula regresar a la vista de inicio (`Inicio.fxml`) manteniendo el proceso de la aplicación vivo para permitir al usuario iniciar una nueva partida.

## 6. ¿Qué arquitectura o regla del repositorio fue más útil para la revisión?
La separación estricta de responsabilidades del patrón **MVC** documentada en `ARCHITECTURE.md` y `AGENTS.md`. Fue fundamental la regla:
> *"src/modelo/ must never import javafx.* or UI components"*.
Esto permitió al reviewer auditar inmediatamente que ni `Partida.java` ni ninguna entidad de dominio dependieran de componentes gráficos, delegando toda la orquestación de navegación y reseteo de pantalla a los controladores (`ControladorPrincipal`).

## 7. ¿Qué problema pudo detectar un check determinista antes que el reviewer-agent?
El script determinista `./scripts/verify_repo.ps1` detectó errores de sintaxis y discrepancias de compilación en Java (como un desbalance de llaves en `ControladorMazosYTurnos.java` durante la edición) antes de que el revisor tuviera que invertir tiempo en analizarlo. Si un cambio no compila o rompe el contrato del repositorio, el Verification Gate lo rechaza de forma inmediata y automática sin consumir el contexto de revisión agéntica.

## 8. ¿Qué parte del review sigue requiriendo criterio humano?
La **priorización del alcance y la decisión de negocio**. Un agente puede sugerir decenas de mejoras válidas (como agregar diálogos de confirmación, internacionalización de textos, animaciones o refactors cosméticos), pero solo el criterio humano puede evaluar el balance entre deuda técnica aceptable, alcance del Sprint/PR y valor de entrega inmediata vs. sobre-ingeniería.

## 9. ¿Qué technical debt aceptaste o diferiste y por qué?
Se aceptó y difirió el hallazgo **RF-05 (Confirmación previa al abandono de partida)**. Se difirió porque el objetivo central de CR-02 era resolver el defecto de sincronización de recursos (`.fxml`/`.css`) y permitir la navegación al menú principal. Añadir la alerta modal se registró como deuda técnica explícita en el backlog para abordarse cuando se rediseñe el sistema global de diálogos del juego.

## 10. ¿Qué artefactos de este ADA reutilizarías en la Agentic Software Factory final?
Sin duda, reutilizaría tres artefactos clave:
1. **`review-contract.md`**: Es la piedra angular para que el revisor automático no alucine ni invente reglas arbitrarias; establece la verdad de qué es "correcto" antes de mirar el diff.
2. **`verify_repo.ps1` (Verification Gate determinista)**: La barrera que garantiza que ningún agente pase código que no compile o que rompa contratos básicos.
3. **`PR_EVIDENCE.md` y `human-review-decisions.md`**: La trazabilidad auditable que documenta qué propuso la IA, qué auditó el revisor y qué autorizó el humano.
