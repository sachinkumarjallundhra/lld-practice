
# Design a Tic-Tac-Toe Game (NxN)

## 📜 Problem Statement

Design a class `TicTacGame` that simulates a Tic-Tac-Toe game played on an **m × m** board between two players.

### Rules
- Players take turns placing marks on empty cells.
- Player 1 uses **A**, Player 2 uses **B**.
- The first player to place **m consecutive symbols** in:
  - a row
  - a column
  - a diagonal  
  wins the game.
- Once a player wins, **no further moves are allowed**.
- Every subsequent move must return the same winner.

---

## 🧠 Design Overview

### Key Observations
- Full board storage is not required.
- Each move affects:
  - one row
  - one column
  - possibly two diagonals
- Winning can be checked in **O(1)** time.

---

## 🏗️ Data Structures Used

| Structure | Purpose |
|---------|---------|
| `int[] rows` | Row-wise score tracking |
| `int[] cols` | Column-wise score tracking |
| `int diag` | Main diagonal score |
| `int antiDiag` | Anti-diagonal score |
| `boolean gameOver` | Locks the game after win |
| `int winner` | Stores the winning player |

### Scoring Technique
- Player 1 → `+1`
- Player 2 → `-1`
- If absolute value reaches `m`, that player wins.

---

## 🧩 Design Patterns Used

- Encapsulation
- State Management
- Optimized Win Detection

---

## 💻 Java Implementation

```java
public class TicTacGame {

    private int m;
    private int[] rows;
    private int[] cols;
    private int diag;
    private int antiDiag;
    private boolean gameOver;
    private int winner;

    public TicTacGame(int m) {
        this.m = m;
        this.rows = new int[m];
        this.cols = new int[m];
        this.diag = 0;
        this.antiDiag = 0;
        this.gameOver = false;
        this.winner = 0;
    }

    /**
     * @param row    row index
     * @param col    column index
     * @param player 1 or 2
     * @return 0 = no winner, 1 = player 1 wins, 2 = player 2 wins
     */
    public int doMove(int row, int col, int player) {

        // If game already ended, always return winner
        if (gameOver) {
            return winner;
        }

        int val = (player == 1) ? 1 : -1;

        rows[row] += val;
        cols[col] += val;

        if (row == col) {
            diag += val;
        }

        if (row + col == m - 1) {
            antiDiag += val;
        }

        if (Math.abs(rows[row]) == m ||
            Math.abs(cols[col]) == m ||
            Math.abs(diag) == m ||
            Math.abs(antiDiag) == m) {

            gameOver = true;
            winner = player;
            return player;
        }

        return 0;
    }
}
```

---

## ⚠️ Edge Cases Handled

* Winning by row, column, or diagonal
* Game lock after win
* Repeated moves return same winner
* Works for large boards (`m ≤ 200`)

---

## ⏱️ Complexity Analysis

| Operation  | Time | Space |
| ---------- | ---- | ----- |
| `doMove()` | O(1) | O(m)  |

---

## 🧪 Example

```
TicTacGame game = new TicTacGame(2);

game.doMove(0, 1, 1); // 0
game.doMove(0, 0, 2); // 0
game.doMove(1, 1, 1); // 1 (Player 1 wins)
game.doMove(1, 0, 2); // 1 (Winner persists)
```

---

## ✅ Interview Notes

* Classic LLD + algorithm optimization problem
* Demonstrates clean state handling
* Shows ability to avoid brute-force board scanning

---

## 🚀 Possible Extensions

* Undo / Redo support
* Move history tracking
* UI rendering
* Multiplayer support


---


