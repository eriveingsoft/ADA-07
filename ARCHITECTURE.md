# Architecture Boundaries

The EBOGE project strictly adheres to the Model-View-Controller (MVC) architectural pattern.

## Layer Definitions & Rules
- `src/modelo/`: Contains core business logic, domain entities (Board, Players, Dice). 
  - **Constraint:** Must never import `javafx.*` or any UI-related libraries.
- `src/vista/`: Contains JavaFX components and FXML layouts.
  - **Constraint:** Must not contain game rules, state mutations, or business logic.
- `src/controlador/`: Orchestrates data flow between the view and the model.

## Protected Paths
- `EBOGE/bin/`: Compiled output. Never edit manually.
- `scripts/`: CI and execution tools. Changes require review.
