# Technical Specifications & Acceptance Criteria

## Acceptance Criteria
- **AC-01 (relates to FR-01):** Given the configuration view, when invalid or empty parameters are submitted, the system halts initialization and alerts the user.
- **AC-13 (relates to FR-13):** Given an active or recently concluded match, when the "Restart" action is triggered, then the `ControladorPrincipal` nullifies the active match state, resets the dice configuration, and loads the configuration view.
