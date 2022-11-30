package proxx

class Utils {
    static int countHolesInAdjustedCells(int row, int col, Board board) {
        int holesAround = 0;
        //walk prev row
        if (isOnBoard(row - 1, col - 1, board.boardSide) && board.isHoleCell(row - 1, col - 1)) {
            holesAround++
        }
        if (isOnBoard(row - 1, col, board.boardSide) && board.isHoleCell(row - 1, col)) {
            holesAround++
        }

        //walk left-right
        if (isOnBoard(row - 1, col + 1, board.boardSide) && board.isHoleCell(row - 1, col + 1)) {
            holesAround++
        }
        if (isOnBoard(row, col - 1, board.boardSide) && board.isHoleCell(row, col - 1)) {
            holesAround++
        }

        //walk next row
        if (isOnBoard(row, col + 1, board.boardSide) && board.isHoleCell(row, col + 1)) {
            holesAround++
        }
        if (isOnBoard(row + 1, col - 1, board.boardSide) && board.isHoleCell(row + 1, col - 1)) {
            holesAround++
        }
        if (isOnBoard(row + 1, col, board.boardSide) && board.isHoleCell(row + 1, col)) {
            holesAround++
        }
        if (isOnBoard(row + 1, col + 1, board.boardSide) && board.isHoleCell(row + 1, col + 1)) {
            holesAround++
        }
        return holesAround
    }

    static boolean isOnBoard(int row, int col, int boardSize) {
        return !(row < 0
                || col < 0
                || row >= boardSize
                || col >= boardSize)
    }

    static Closure holeIndexPredicate = { int flatIndex, Board board ->
        board.isHoleCell(flatIndex / board.getBoardSide() as int, flatIndex % board.getBoardSide())
    }

    static Closure emptyIndexPredicate = { int flatIndex, Board board ->
        board.isEmptyCell(flatIndex / board.getBoardSide() as int, flatIndex % board.getBoardSide())
    }
    static Closure openIndexPredicate = { int flatIndex, Board board ->
        board.isCellOpen(flatIndex / board.getBoardSide() as int, flatIndex % board.getBoardSide())
    }

    static Closure adjacentIndexPredicate = { int flatIndex, Board board ->
        board.isAdjacentCell(flatIndex / board.getBoardSide() as int, flatIndex % board.getBoardSide())
    }

    static Closure assertHoleHit =
            { Board actualBoard, int hitFlatIndex ->
                assert actualBoard.getCellContent(hitFlatIndex / actualBoard.getBoardSide() as int, hitFlatIndex % actualBoard.getBoardSide()) == Board.HOLE
                assert actualBoard.isBoardFailed()
                //all cells are open
                (0..actualBoard.getBoardSide() * actualBoard.getBoardSide() - 1).each {
                    if (actualBoard.isHoleCell(((int) (it / actualBoard.getBoardSide())), it % actualBoard.getBoardSide())) {
                        assert actualBoard.isCellOpen(((int) (it / actualBoard.getBoardSide())), it % actualBoard.getBoardSide())
                    }
                }
                return true
            }

    static Closure assertEmptyHit =
            { Board actualBoard, int hitFlatIndex ->
                assert actualBoard.getCellContent(hitFlatIndex / actualBoard.getBoardSide() as int, hitFlatIndex % actualBoard.getBoardSide()) == 0
                assert !actualBoard.isBoardFailed()

                validateEmptySurrounding(actualBoard, hitFlatIndex, new HashSet<>())
                return true
            }

    static Closure assertAdjacentHit =
            { Board actualBoard, int hitFlatIndex ->
                assert actualBoard.getCellContent(hitFlatIndex / actualBoard.getBoardSide() as int, hitFlatIndex % actualBoard.getBoardSide()) > 0
                assert !actualBoard.isBoardFailed()

                // exact cell under hit is open
                assert actualBoard.isCellOpen(hitFlatIndex / actualBoard.getBoardSide() as int, hitFlatIndex % actualBoard.getBoardSide())
                return true
            }

    static void validateEmptySurrounding(Board board, int flatIndex, Set<Integer> validatedFlatIndexes) {
        int row = flatIndex / board.getBoardSide() as int
        int col = flatIndex % board.getBoardSide()

        validateEmptySurrounding(board, row, col, validatedFlatIndexes)
    }

    static void validateEmptySurrounding(Board board, int row, int col, Set<Integer> validatedFlatIndexes) {
        //validate prev row
        validateCellNearEmpty(board, row - 1, col - 1, validatedFlatIndexes)
        validateCellNearEmpty(board, row - 1, col, validatedFlatIndexes)
        validateCellNearEmpty(board, row - 1, col + 1, validatedFlatIndexes)

        //validate left-right cols
        validateCellNearEmpty(board, row, col - 1, validatedFlatIndexes)
        validateCellNearEmpty(board, row, col + 1, validatedFlatIndexes)

        //validate next row
        validateCellNearEmpty(board, row + 1, col - 1, validatedFlatIndexes)
        validateCellNearEmpty(board, row + 1, col, validatedFlatIndexes)
        validateCellNearEmpty(board, row - +1, col + 1, validatedFlatIndexes)
    }

    static void validateCellNearEmpty(Board board, int row, int col, Set<Integer> validatedFlatIndexes) {
        if (!isOnBoard(row, col, board.getBoardSide()) || validatedFlatIndexes.contains(row * board.getBoardSide() + col)) {
            return
        }

        assert board.isCellOpen(row, col)
        assert !board.isHoleCell(row, col)
        assert board.isAdjacentCell(row, col) || board.isEmptyCell(row, col)

        validatedFlatIndexes.add(row * board.getBoardSide() + col)

        if (board.isEmptyCell(row, col)) {
            validateEmptySurrounding(board, row, col, validatedFlatIndexes)
        }

    }

    static void printBoard(Board board) {
        println("\n\rBoard $board.boardSide x $board.boardSide")
        println("* - hole \n\r. - empty\n\r")
        (0..board.boardSide - 1).each { row ->
            println()
            (0..board.boardSide - 1).each { col ->
                if (board.isHoleCell(row, col)) {
                    print("*\t")
                } else if (board.isEmptyCell(row, col)) {
                    print(".\t")
                } else {
                    print(board.getCellContent(row, col) + "\t")
                }
            }
        }
        println()
    }
}
