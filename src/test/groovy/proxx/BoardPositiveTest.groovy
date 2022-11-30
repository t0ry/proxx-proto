package proxx

import spock.lang.Specification
import spock.lang.Unroll

import static proxx.Utils.*

class BoardPositiveTest extends Specification {

    def "must init board when initWith() invoked with proper arguments"() {
        given:
        int boardSize = 10
        int holesNumber = 20

        when:
        Board actualBoard = Board.initWith(boardSize, holesNumber)

        then:
        // not needed for tests; just for visualization purpose
        printBoard(actualBoard)

        assert !actualBoard.isBoardFailed()

        and:
        int actualHolesNumber = 0
        int actualClosedNumber = 0
        (0..boardSize - 1).each { row ->
            (0..boardSize - 1).each { col ->
                // gather closed cells
                if (!actualBoard.isCellOpen(row, col)) {
                    actualClosedNumber++
                }

                // verify hole content and gather holes
                if (actualBoard.isHoleCell(row, col)) {
                    assert actualBoard.getCellContent(row, col) == Board.HOLE
                    actualHolesNumber++
                }
                //verify empty content
                else if (actualBoard.isEmptyCell(row, col)) {
                    assert actualBoard.getCellContent(row, col) == Board.EMPTY
                }
                //verify cells with holes around
                else {
                    assert actualBoard.getCellContent(row, col) > 0 && actualBoard.getCellContent(row, col) <= 8

                    int expectedContent = countHolesInAdjustedCells(row, col, actualBoard)
                    assert expectedContent == actualBoard.getCellContent(row, col)
                }
            }
        }
        assert holesNumber == actualHolesNumber // verify all holes on board
        assert actualClosedNumber == boardSize * boardSize //verify all cells closed after init
    }

    @Unroll
    def "must return true/false if isCellOpen() invoked on open/closed cell"() {
        given:
        int boardSize = 5
        int holesNumber = 10
        Board board = Board.initWith(boardSize, holesNumber)
        int flatAdjacentIndex = (0..boardSize * boardSize - 1).findAll { it -> adjacentIndexPredicate it, board }[0]
        int flatHoleIndex = (0..boardSize * boardSize - 1).findAll { it -> holeIndexPredicate it, board }[0]
        int flatEmptyIndex = (0..boardSize * boardSize - 1).findAll { it -> emptyIndexPredicate it, board }[0]

        when:
        Board boardAfterHit = board.hitCell(flatAdjacentIndex / boardSize as int, flatAdjacentIndex % boardSize)

        then:
        assert boardAfterHit.isCellOpen(flatAdjacentIndex / boardSize as int, flatAdjacentIndex % boardSize)
        assert !boardAfterHit.isCellOpen(flatHoleIndex / boardSize as int, flatHoleIndex % boardSize)
        assert !boardAfterHit.isCellOpen(flatEmptyIndex / boardSize as int, flatEmptyIndex % boardSize)
    }

    @Unroll
    def "must return #expected if isHoleCell(..) invoked on #cellContent cell"() {
        given:
        int boardSize = 5
        int holesNumber = 5
        Board board = Board.initWith(boardSize, holesNumber)
        int flatIndex = (0..boardSize * boardSize - 1).findAll { it -> indexPredicate it, board }[0]

        when:
        boolean actual = board.isHoleCell(flatIndex / boardSize as int, flatIndex % boardSize)

        then:
        assert actual == expected

        where:
        cellContent | indexPredicate         || expected
        "hole"      | holeIndexPredicate     || true
        "adjacent"  | adjacentIndexPredicate || false
        "empty"     | emptyIndexPredicate    || false

    }

    @Unroll
    def "must return #expected if isEmptyCell(..) invoked on #cellContent cell"() {
        given:
        int boardSize = 5
        int holesNumber = 5
        Board board = Board.initWith(boardSize, holesNumber)
        int flatIndex = (0..boardSize * boardSize - 1).findAll { it -> indexPredicate it, board }[0]

        when:
        boolean actual = board.isEmptyCell(flatIndex / boardSize as int, flatIndex % boardSize)

        then:
        assert actual == expected

        where:
        cellContent | indexPredicate         || expected
        "hole"      | holeIndexPredicate     || false
        "adjacent"  | adjacentIndexPredicate || false
        "empty"     | emptyIndexPredicate    || true

    }

    @Unroll
    def "must return #expected if isAdjacentCell(..) invoked on #cellContent cell"() {
        given:
        int boardSize = 5
        int holesNumber = 5
        Board board = Board.initWith(boardSize, holesNumber)
        int flatIndex = (0..boardSize * boardSize - 1).findAll { it -> indexPredicate it, board }[0]

        when:
        boolean actual = board.isAdjacentCell(flatIndex / boardSize as int, flatIndex % boardSize)

        then:
        assert actual == expected

        where:
        cellContent | indexPredicate         || expected
        "hole"      | holeIndexPredicate     || false
        "adjacent"  | adjacentIndexPredicate || true
        "empty"     | emptyIndexPredicate    || false

    }

    @Unroll
    def "must return cell content if getCellContent(..) invoked on #cellContent cell"() {
        given:
        int boardSize = 5
        int holesNumber = 5
        Board board = Board.initWith(boardSize, holesNumber)
        int flatIndex = (0..boardSize * boardSize - 1).findAll { it -> indexPredicate it, board }[0]

        when:
        int actual = board.getCellContent(flatIndex / boardSize as int, flatIndex % boardSize)

        then:
        assert validator.call(actual)

        where:
        cellContent | indexPredicate         || validator
        "hole"      | holeIndexPredicate     || { actualValue -> actualValue == Board.HOLE }
        "adjacent"  | adjacentIndexPredicate || { actualValue -> actualValue > Board.ADJACENT_THRESHOLD }
        "empty"     | emptyIndexPredicate    || { actualValue -> actualValue == Board.EMPTY }

    }

    @Unroll
    def "must update board state accordingly when invoked hitCell(...) on #testTitle"() {
        given:
        int boardSize = 10
        int holesNumber = 20

        Board initialBoard = Board.initWith(boardSize, holesNumber)

        // in fact only single index is required, however with array of indexes hit on random index of hole/empty/adjacent cell can be introduced
        List flatIndexesToHit = (0..boardSize * boardSize - 1).findAll { it -> cellFlatIndexPredicate it, initialBoard }

        when:
        Board actualBoard = initialBoard.hitCell(flatIndexesToHit[0] / boardSize as int, flatIndexesToHit[0] % boardSize)

        then:
        validator actualBoard, flatIndexesToHit[0]

        where:
        testTitle           | cellFlatIndexPredicate || validator
        "a hole "           | holeIndexPredicate     || assertHoleHit
        "an empty cell "    | emptyIndexPredicate    || assertEmptyHit
        "an adjacent cell " | adjacentIndexPredicate || assertAdjacentHit
    }

    @Unroll
    def "must not update board state invoked hitCell(...) on failed board"() {
        given:
        int boardSize = 10
        int holesNumber = 20

        Board initialBoard = Board.initWith(boardSize, holesNumber)

        int flatHoleIndexToHit = (0..boardSize * boardSize - 1).findAll { it -> holeIndexPredicate it, initialBoard }[0]

        // fail board hitting hole cell
        Board actualBoardAfterFirstHit = initialBoard.hitCell(flatHoleIndexToHit / boardSize as int, flatHoleIndexToHit % boardSize)

        List expectedOpenFlatIndexes = (0..boardSize * boardSize - 1).findAll { it -> openIndexPredicate it, actualBoardAfterFirstHit }

        when: //hit already open cell - the same hole
        Board actualBoardAfterSecondHitOnOpenCell = initialBoard.hitCell(flatHoleIndexToHit / boardSize as int, flatHoleIndexToHit % boardSize)

        then:
        List actualOpenFlatIndexesAfterSecondHit = (0..boardSize * boardSize - 1).findAll { it -> openIndexPredicate it, actualBoardAfterSecondHitOnOpenCell }
        assert actualOpenFlatIndexesAfterSecondHit == expectedOpenFlatIndexes

        when: // hit closed cell - any
        int closedFlatIndexToHit = (0..boardSize * boardSize - 1).findAll { it -> !expectedOpenFlatIndexes.contains(it) }[0]
        Board actualBoardAfterThirdHitOnClosedCell = initialBoard.hitCell(closedFlatIndexToHit / boardSize as int, closedFlatIndexToHit % boardSize)

        then:
        List actualOpenFlatIndexesAfterThirdHit = (0..boardSize * boardSize - 1).findAll { it -> openIndexPredicate it, actualBoardAfterThirdHitOnClosedCell }
        assert actualOpenFlatIndexesAfterThirdHit == expectedOpenFlatIndexes
    }
}
