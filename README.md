# proxx-proto
Solution represents a prototype of **[Proxx](https://proxx.app)** game.

<p>Prototype does not include flags and works with square border `NxN`.

## Initial requirements

### Part 1:
Choose a data structure(s) to represent the game state. You need to keep track of the following:
- NxN board
- Location of black holes
- Counts of # of adjacent black holes
- Whether a cell is open

### Part 2:
Populate your data structure with K black holes placed in random locations. Note that should
place exactly K black holes and their location should be random with a uniform distribution.

### Part 3
For each cell without a black hole, compute and store the number of adjacent black holes. Note
that diagonals also count. E.g.

0 2 H  
1 3 H  
H 2 1

### Part 4
Write the logic that updates which cells become visible when a cell is clicked. Note that if a cell
has zero adjacent black holes the game needs to automatically make the surrounding cells
visible.


## How to use it

Snipped of code as an example.

```java
class Game {

    // HitProvider and Receiver are references to client implementations. 
    // It's totally up to client how to provide cell position and handle hit result.
    public void play (HitProvider hitProvider, Reciever receiver) {
        // init the board
        int n = 5; // board side
        int k = 10; // holesNumber
        Board board = Board.initWith(n, k);
        
        while (board.isBoardInProgress()) {
        
            // get position to hit
            // position might be obtained from aby possible source: user input (console, UI action), events based,  random etc
            int row = hitProvider.getPositionX(); // up to client
            int col = hitProvider.getPositionY(); //up to client
        
            //do hit on a cell placed in position (row, col)
            board.hitCell(row, col);
        
            //check if board got failed i.e. cell with a hole has got hit on current step or
            // if board got succeeded i.e. all cells except holes got open - game over
            if (board.isBoardFailed() || board.isBoardSucceeded()) {
                break;
            }
        
            // check if hit has been done in adjacent cell
            if (board.isAdjacentCell(row, col) && board.isCellOpen(row, col)) {
                int content = board.getCellContent(row, col);
        
                // draw/show/reflect in any way cell content namely number of adjacent holes
                receiver.showCellContent(content); // up to client
            } else { // hit appeared on empty cell, refresh all cells open
                for (int i = 0; i < board.getBoardSide(); i++) {
                    for (int j = 0; j < board.getBoardSide(); j++) {
                        if (board.isCellOpen(i, j)) {
                            receiver.showCellContent(board.getCellContent(i, j)); // up to client
                        }
                    }
                }
            }
        }
        
        if(board.isBoardFailed()){
            //  do what you want to do on failure
            // for instance, show all open cells
            for (int i = 0; i < board.getBoardSide(); i++) {
                for (int j = 0; j < board.getBoardSide(); j++) {
                    if (board.isCellOpen(i, j)) {
                        receiver.showCellContent(board.getCellContent(i, j)); // up to client
                    }
                }
            }
            receiver.showFailure();
        }else if(board.isBoardSucceeded()){
            //do what you want to do
            reciever.showSuccess();
        }
    }
}
```
