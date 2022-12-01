package proxx;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents Proxx board which holds the game itself and allows hitting cells as well as obtaining cells state and content.
 * No flags.
 */
public final class Board {
    static final int HOLE = -1;
    static final int EMPTY = 0;
    static final int ADJACENT_THRESHOLD = 0;

    private static final int SIDE_UPPER_LIMIT = 40;
    private static final int SIDE_LOWER_LIMIT = 5;
    private static final int HOLES_LOWER_LIMIT = 1;
    private static final int HOLES_UPPER_PADDING = 9;


    private static final BiPredicate<Integer, Integer> BELOW_BORDER = (value, border) -> value < border;
    private static final BiPredicate<Integer, Integer> ABOVE_BORDER = (value, border) -> value > border;
    private static final Predicate<Integer> BELOW_ZERO = (value) -> BELOW_BORDER.test(value, 0);
    private static final BiPredicate<Integer, Integer> OUT_OF_BORDER_UPPER_INCLUSION
            = (value, border) -> BELOW_ZERO.test(value) || ABOVE_BORDER.test(value, border - 1);

    private enum BoardState {
        FAILED, IN_PROGRESS, SUCCEEDED
    }

    private final int[] board;
    private final boolean[] cellsOpenState;
    private final int holesNumber;
    private final int boardSide;

    private int openCellsCount;

    private BoardState boardState = BoardState.IN_PROGRESS;

    private Board(final int boardSize, final int holesNumber) {
        this.board = new int[boardSize * boardSize]; // all empty initially
        this.cellsOpenState = new boolean[boardSize * boardSize]; // all closed initially
        this.boardSide = boardSize;
        this.holesNumber = holesNumber;
    }

    private static Set<Integer> generateHolesPositions(final int holeNumbers, final int range) {
        return new Random().ints(0, range)
                .distinct()
                .limit(holeNumbers)
                .boxed()
                .collect(Collectors.toSet());
    }

    public static Board initWith(final int boardSize, final int holesNumber) {
        if (BELOW_BORDER.test(boardSize, SIDE_LOWER_LIMIT)) {
            throw new IllegalArgumentException(String.format("'boardSize' must be greater than or equal to %s, but got '%s'.",
                    SIDE_LOWER_LIMIT,
                    boardSize));
        }

        if (ABOVE_BORDER.test(boardSize, SIDE_UPPER_LIMIT)) {
            throw new IllegalArgumentException(String.format("'boardSize' must be less than or equal to %s, but got '%s'.",
                    SIDE_UPPER_LIMIT,
                    boardSize));
        }

        if (BELOW_BORDER.test(holesNumber, HOLES_LOWER_LIMIT)) {
            throw new IllegalArgumentException(String.format("'holesNumber' must be greater than or equal to %s, but got '%s'.",
                    HOLES_LOWER_LIMIT,
                    holesNumber));
        }

        if (ABOVE_BORDER.test(holesNumber, boardSize * boardSize - HOLES_UPPER_PADDING)) {
            throw new IllegalArgumentException(String.format("'holesNumber' must be less than or equal to %s, but got '%s'.",
                    boardSize * boardSize - HOLES_UPPER_PADDING,
                    holesNumber));
        }

        final Board board = new Board(boardSize, holesNumber);
        board.placeHoles(holesNumber);

        return board;
    }

    private int[] placeHoles(final int holeNumbers) {
        final Set<Integer> holesPositions = generateHolesPositions(holeNumbers, ((boardSide * boardSide)));

        final Iterator<Integer> iterator = holesPositions.iterator();
        while (iterator.hasNext()) {
            int holePosition = iterator.next();

            //place hole
            this.board[holePosition] = HOLE;

            int i = (holePosition / boardSide);
            int j = (holePosition % boardSide);

            // adjust virtual prev. row
            adjustCell((i - 1), (j - 1));
            adjustCell((i - 1), j);
            adjustCell((i - 1), (j + 1));

            // adjust left-right cells
            adjustCell(i, (j - 1));
            adjustCell(i, (j + 1));

            // adjust virtual next row
            adjustCell((i + 1), (j - 1));
            adjustCell((i + 1), j);
            adjustCell((i + 1), (j + 1));
        }
        return board;
    }

    private void adjustCell(final int row, final int col) {
        final int position = (row * boardSide + col);

        //skip out of bounds and holes
        if (OUT_OF_BORDER_UPPER_INCLUSION.test(row, boardSide)
                || OUT_OF_BORDER_UPPER_INCLUSION.test(col, boardSide)
                || board[position] == HOLE) {
            return;
        }

        board[position]++;
    }

    private Board doHit(final int row, final int col) {
        //skip cell out of board
        if (OUT_OF_BORDER_UPPER_INCLUSION.test(row, boardSide) || OUT_OF_BORDER_UPPER_INCLUSION.test(col, boardSide)) {
            return this;
        }

        final int flatPosition = (row * boardSide + col);

        // cell is already open - nothing to do with data content
        if (cellsOpenState[flatPosition] == true) {
            return this;
        }

        //cell is a hole - open all holes, fail board
        if (board[flatPosition] == HOLE) {
            processHole();
        }
        // cell is adjacent to hole(s) - just open
        else if (board[flatPosition] > 0) {
            processAdjacent(flatPosition);
        }
        //empty - no holes around, so initiate hits around the empty cell
        else if (board[flatPosition] == EMPTY) {
            cellsOpenState[flatPosition] = true;
            processEmpty(row, col);
        }

        openCellsCount++;

        // if no hits on holes happened and all cells except holes are open - win the board
        if (!isBoardFailed() && (openCellsCount == cellsOpenState.length - holesNumber)) {
            boardState = BoardState.SUCCEEDED;
        }

        return this;
    }

    private void processEmpty(final int row, final int col) {
        // hit virtual prev. row
        doHit(row - 1, col - 1);
        doHit(row - 1, col);
        doHit(row - 1, (col + 1));

        // hit left-right cells
        doHit(row, col - 1);
        doHit(row, (col + 1));

        // hit virtual next row
        doHit((row + 1), col - 1);
        doHit((row + 1), col);
        doHit((row + 1), (col + 1));
    }

    private void processAdjacent(final int singleDimensionalPosition) {
        cellsOpenState[singleDimensionalPosition] = true;
    }

    private void processHole() {
        for (int i = 0; i < cellsOpenState.length; i++) {
            if (board[i] == HOLE) {
                cellsOpenState[i] = true;
            }
        }
        this.boardState = BoardState.FAILED;
    }

    private void assertIndexes(final int row, final int col) {
        if (OUT_OF_BORDER_UPPER_INCLUSION.test(row, boardSide)) {
            throw new IllegalArgumentException(String.format("'row' must be in range [0..%s], but is '%s'.", boardSide, row));
        }
        if (OUT_OF_BORDER_UPPER_INCLUSION.test(col, boardSide)) {
            throw new IllegalArgumentException(String.format("'col' must be in range [0..%s], but is '%s'.", boardSide, col));
        }
    }

    /**
     * Indicates if cell in position (<code>row</code>,  <code>col</code>) is open i.e. got previously hit or
     * opened automatically.
     */
    public boolean isCellOpen(final int row, final int col) {
        assertIndexes(row, col);
        return cellsOpenState[row * boardSide + col];
    }

    /**
     * Indicates if cell in position (<code>row</code>,  <code>col</code>) is empty i.e. no adjacent holes.
     */
    public boolean isEmptyCell(final int row, final int col) {
        assertIndexes(row, col);
        return board[row * boardSide + col] == EMPTY;
    }

    /**
     * Indicates if cell in position (<code>row</code>,  <code>col</code>) is a hole.
     */
    public boolean isHoleCell(final int row, final int col) {
        assertIndexes(row, col);
        return board[row * boardSide + col] == HOLE;
    }

    /**
     * Indicates if cell in position (<code>row</code>,  <code>col</code>) is adjacent to a hole(s)
     * i.e. contains number of adjacent holes.
     */
    public boolean isAdjacentCell(final int row, final int col) {
        assertIndexes(row, col);
        return board[row * boardSide + col] > ADJACENT_THRESHOLD;
    }

    /**
     * Provides content for the cell in position (<code>row</code>,  <code>col</code>).
     * If a is array of contents then
     * <ul>
     * <li>a[i]==-1 means a hole
     * <li>a[i]== 0 means an empty cell
     * <li>a[i]== 1..8 means number of adjacent holes around the cell
     * </ul>
     *
     * @return cell content
     */
    public int getCellContent(final int row, final int col) {
        assertIndexes(row, col);
        return board[row * boardSide + col];
    }

    public int getBoardSide() {
        return boardSide;
    }

    /**
     * Indicates if hole cell has been hit.
     */
    public boolean isBoardFailed() {
        return boardState == BoardState.FAILED;
    }

    /**
     * Indicates if board (game) is still in progress: no hits on holes, not all hon-hole cells got open.
     */
    public boolean isBoardInProgress() {
        return boardState == BoardState.IN_PROGRESS;
    }


    /**
     * Indicates if board (game) is succeeded: no hits on holes, all hon-hole cells got open.
     */
    public boolean isBoardSucceeded() {
        return boardState == BoardState.SUCCEEDED;
    }

    /**
     * Allows hitting cell in position (<code>row</code>,  <code>col</code>) updating cells state (open/closed) accordingly.
     * If board is in <code>failed</code> state, no updates applied.
     *
     * @return board  - the board updated after hit
     */
    public Board hitCell(final int row, final int col) {
        assertIndexes(row, col);

        // nothing to do if board has already failed.
        if (isBoardFailed()) {
            return this;
        }

        return doHit(row, col);
    }
}