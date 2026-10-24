# Task Under Test

Change ID: CR-01

## Goal
Implement a "Restart Game" (Reiniciar) functionality when a player reaches the victory condition. Add a button or option in the victory dialog/screen that completely resets the game state and returns the user to the initial configuration screen, without requiring them to close and reopen the application.

## Requirements Covered
- RF-13

## Verification
Launch the application, start a game, and force a player to reach the finish line (casilla meta). When the victory screen/dialog appears, click the new "Restart" button. Verify that the application successfully returns to the player configuration screen and the previous game state is cleared.

## Constraints
- Use ERS_EBOGE.md as the source of truth.
- Keep changes scoped to this task.
- Do not redefine requirements in this file.