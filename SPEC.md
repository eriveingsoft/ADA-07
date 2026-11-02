# Technical Specifications & Acceptance Criteria

## Acceptance Criteria
- **AC-01 (relates to FR-01):** Given the configuration view, when invalid or empty parameters are submitted, the system halts initialization and alerts the user.
- **AC-13 (relates to FR-13):** Given an active or recently concluded match, when the "Restart" action is triggered, then the `ControladorPrincipal` nullifies the active match state, resets the dice configuration, and loads the configuration view.
- **AC-16 (relates to FR-16):** Given an active match view, when the "Salir al Menú" action is triggered, then `ControladorPrincipal` resets the match state and navigates back to `Inicio.fxml`. In addition, the repository build mechanism must synchronize all view assets (`.fxml` and `.css`) to the runtime directory (`EBOGE/bin/Vista/`) so that all interface components render properly.
