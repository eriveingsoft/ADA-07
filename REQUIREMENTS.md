# Software Requirements Specification (EBOGE)

## Source of Truth
This document is the single source of truth for the system's functional capabilities.

## Functional Requirements
- **FR-01 (Player Validation):** The system must validate all player configuration parameters before allowing a match to begin.
- **FR-13 (Match Restart - CR-01):** The system must provide a mechanism to reset the active match state (players, board, dice) and return the user to the initial configuration screen without terminating the application process.
- **FR-16 (Match Exit to Main Menu - CR-02):** The system must provide a mechanism during an active match to abandon the game and return the user cleanly to the main menu screen (`Inicio.fxml`), resetting active match state without terminating the application process.
