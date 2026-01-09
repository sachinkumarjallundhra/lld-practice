10353. Design Snake Game

Create a Snake game simulator played on a screen with given rows and cols dimensions. The snake starts at the top-left cell (0,0) with a length of 1.

The game is provided with a list of food positions, given as row-column pairs. When the snake's head moves onto a food cell, the snake grows by 1 and the score increases by 1. New food only appears after the previous one is eaten, and food never appears where the snake is.

Implement a class SnakeGame with:

A constructor SnakeGame(int rows, int cols, String[] foodPositions) where each string in foodPositions contains the row and column separated by a comma (e.g. "1,2").
A method int move(String direction) where direction is one of "U" (up), "D" (down), "L" (left), or "R" (right). This moves the snake one step in the given direction. Returns the score after the move, or -1 if the game ends (by running into the wall or itself).
Examples
Input:
SnakeGame game = new SnakeGame(2, 3, new String[]{"1,1","0,2"});

game.move("R"); // returns 0

game.move("D"); // returns 0

game.move("R"); // returns 1 (snake eats food at (1,1))

game.move("U"); // returns 1

game.move("R"); // returns 2 (snake eats food at (0,2))

game.move("D"); // returns -1 (snake hits the wall)
Constraints
1 ≤ rows, cols ≤ 100
0 ≤ foodPositions.length ≤ 200
Each food position is a string "r,c" with 0 ≤ r < rows and 0 ≤ c < cols
Directions are always one of "U", "D", "L", "R"