# Design Snake Game

## Problem Statement

Design a Snake game simulator played on a 2D grid.

* The grid has fixed dimensions `rows × cols`
* The snake starts at position `(0,0)` with length `1`
* The snake moves one step at a time in one of the directions:

    * `"U"` (up), `"D"` (down), `"L"` (left), `"R"` (right)
* Food positions are provided in advance and appear sequentially
* When the snake eats food:

    * Snake length increases by 1
    * Score increases by 1
* The game ends if:

    * The snake hits the wall
    * The snake hits itself
* After the game ends, **all further moves must return `-1`**

---

## Design Overview

The snake is represented as an ordered sequence of cells.
Each move adds a new head and conditionally removes the tail.

### Key Design Decisions

* Use a **Deque** to represent the snake body
* Use a **HashSet** for fast self-collision detection
* Use a **Queue** to store food positions
* Maintain a `gameOver` flag to handle post-termination behavior

---

## Data Structures

| Purpose         | Structure          |
| --------------- | ------------------ |
| Snake body      | `Deque<Cell>`      |
| Collision check | `HashSet<Cell>`    |
| Food sequence   | `Queue<Cell>`      |
| Game state      | `boolean gameOver` |

---

## Core Entities

### Cell

Represents a position on the grid.

```java
class Cell {
    int row;
    int col;

    Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return row == cell.row && col == cell.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
```

---

## Design Patterns Used

* Encapsulation
* State management via `gameOver` flag
* Efficient simulation using Deque
* HashSet for constant-time collision detection

---

## Java Implementation

```java
import java.util.*;

public class SnakeGame {

    private int rows;
    private int cols;
    private Deque<Cell> snake;
    private Set<Cell> occupied;
    private Queue<Cell> foodQueue;
    private int score;
    private boolean gameOver;

    public SnakeGame(int rows, int cols, String[] foodPositions) {
        this.rows = rows;
        this.cols = cols;
        this.snake = new LinkedList<>();
        this.occupied = new HashSet<>();
        this.foodQueue = new LinkedList<>();
        this.score = 0;
        this.gameOver = false;

        Cell start = new Cell(0, 0);
        snake.addLast(start);
        occupied.add(start);

        for (String food : foodPositions) {
            String[] parts = food.split(",");
            foodQueue.offer(
                new Cell(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1])
                )
            );
        }
    }

    public int move(String direction) {
        if (gameOver) {
            return -1;
        }

        Cell head = snake.peekLast();
        int r = head.row;
        int c = head.col;

        switch (direction) {
            case "U": r--; break;
            case "D": r++; break;
            case "L": c--; break;
            case "R": c++; break;
        }

        Cell newHead = new Cell(r, c);

        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            gameOver = true;
            return -1;
        }

        boolean eatsFood =
            !foodQueue.isEmpty() && foodQueue.peek().equals(newHead);

        if (!eatsFood) {
            Cell tail = snake.pollFirst();
            occupied.remove(tail);
        }

        if (occupied.contains(newHead)) {
            gameOver = true;
            return -1;
        }

        snake.addLast(newHead);
        occupied.add(newHead);

        if (eatsFood) {
            foodQueue.poll();
            score++;
        }

        return score;
    }
}
```

---

## Edge Cases Handled

* Wall collision
* Self collision
* Moves after game over
* No food scenario
* Snake moving into previous tail position
* Empty food list

---

## Time and Space Complexity

### move(direction)

* Time Complexity: **O(1)**
* Space Complexity: **O(rows × cols)**

---

## Example

```java
SnakeGame game = new SnakeGame(2, 2, new String[]{});

game.move("R"); // 0
game.move("D"); // 0
game.move("R"); // -1
game.move("U"); // -1
```

---

## Interview Notes

* Always remove the tail before checking self-collision
* Persist game-over state
* Deque + HashSet is the optimal combination
* Follow inclusive boundary rules strictly

---

If you want, next I can:

* Add this to `mkdocs.yml` navigation
* Convert **Text Editor (Undo/Redo)** to the same format
* Create a **reusable LLD markdown template**
* Refactor code for **interview-grade clarity**

Just tell me what’s next.
