package lld.machinecoding.designsnakegame;

import java.util.*;

public class SnakeGame {

    private int rows, cols;
    private Deque<Cell> snake;
    private Set<Cell> occupied;
    private Queue<Cell> foodQueue;
    private int score;
    private boolean gameOver;
    // ---------------- Helper Class ----------------

    private static class Cell {
        int row, col;

        Cell(int r, int c) {
            row = r;
            col = c;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Cell)) return false;
            Cell other = (Cell) o;
            return row == other.row && col == other.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }

    public SnakeGame(int rows, int cols, String[] foodPositions) {
        this.rows = rows;
        this.cols = cols;
        this.snake = new ArrayDeque<>();
        this.occupied = new HashSet<>();
        this.foodQueue = new ArrayDeque<>();
        this.score = 0;
        this.gameOver = false;

        // Initial snake position
        Cell start = new Cell(0, 0);
        snake.addLast(start);
        occupied.add(start);

        // Load food positions
        for (String pos : foodPositions) {
            String[] parts = pos.split(",");
            foodQueue.add(new Cell(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1])
            ));
        }
    }

    public int move(String direction) {
        if (gameOver) {
            return -1;
        }

        Cell head = snake.peekLast();
        int newRow = head.row;
        int newCol = head.col;

        switch (direction) {
            case "U": newRow--; break;
            case "D": newRow++; break;
            case "L": newCol--; break;
            case "R": newCol++; break;
        }

        Cell newHead = new Cell(newRow, newCol);

        // Wall collision
        if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) {
            gameOver = true;
            return -1;
        }

        // Remove tail temporarily
        Cell tail = snake.peekFirst();
        boolean eatsFood = !foodQueue.isEmpty() && foodQueue.peek().equals(newHead);

        if (!eatsFood) {
            snake.pollFirst();
            occupied.remove(tail);
        }

        // Self collision
        if (occupied.contains(newHead)) {
            gameOver = true;
            return -1;
        }

        // Add new head
        snake.addLast(newHead);
        occupied.add(newHead);

        // Handle food
        if (eatsFood) {
            foodQueue.poll();
            score++;
        }

        return score;

    }
}
