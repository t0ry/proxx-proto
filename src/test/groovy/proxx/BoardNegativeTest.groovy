package proxx

import spock.lang.Specification
import spock.lang.Unroll

class BoardNegativeTest extends Specification {
    @Unroll
    def "must throw an exception when initWith() invoked with #testTitle"() {

        when:
        Board.initWith(boardSize as int, holesNumber as int)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == expecetedMessage

        where:
        testTitle                     | boardSize | holesNumber               || expecetedMessage
        "board side < allowed"        | 4         | 10                        || "'boardSize' must be greater than or equal to 5, but got '4'."
        "board side > allowed"        | 41        | 10                        || "'boardSize' must be less than or equal to 40, but got '41'."
        "holes number < allowed"      | 5         | 0                         || "'holesNumber' must be greater than or equal to 1, but got '0'."
        "holes number side > allowed" | 5         | boardSize * boardSize - 8 || String.format("'holesNumber' must be less than or equal to %s, but got '%s'.", (boardSize * boardSize - 9), holesNumber)
    }

    @Unroll
    def "must throw an exception when hitCell(...) invoked with #testTitle"() {

        given:
        int holesNumber = 7
        Board initialBoard = Board.initWith(boardSize, holesNumber)

        when:
        initialBoard.hitCell(row, col)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == expecetedMessage

        where:
        testTitle         | boardSize | row       | col       || expecetedMessage
        "row < 0"         | 5         | -1        | 0         || String.format("'row' must be in range [0..5], but is '%s'.", row)
        "row>=board side" | 5         | boardSize | 0         || String.format("'row' must be in range [0..5], but is '%s'.", row)
        "col < 0"         | 5         | 0         | -1        || String.format("'col' must be in range [0..5], but is '%s'.", col)
        "col>=board side" | 5         | 0         | boardSize || String.format("'col' must be in range [0..5], but is '%s'.", col)
    }

    @Unroll
    def "must throw an exception when isHoleCell(...) invoked with #testTitle"() {

        given:
        int holesNumber = 7
        Board initialBoard = Board.initWith(boardSize, holesNumber)

        when:
        initialBoard.isHoleCell(row, col)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == expecetedMessage

        where:
        testTitle         | boardSize | row       | col       || expecetedMessage
        "row < 0"         | 5         | -1        | 0         || String.format("'row' must be in range [0..5], but is '%s'.", row)
        "row>=board side" | 5         | boardSize | 0         || String.format("'row' must be in range [0..5], but is '%s'.", row)
        "col < 0"         | 5         | 0         | -1        || String.format("'col' must be in range [0..5], but is '%s'.", col)
        "col>=board side" | 5         | 0         | boardSize || String.format("'col' must be in range [0..5], but is '%s'.", col)
    }

    @Unroll
    def "must throw an exception when isAdjacentCell(...) invoked with #testTitle"() {

        given:
        int holesNumber = 7
        Board initialBoard = Board.initWith(boardSize, holesNumber)

        when:
        initialBoard.isAdjacentCell(row, col)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == expecetedMessage

        where:
        testTitle         | boardSize | row       | col       || expecetedMessage
        "row < 0"         | 5         | -1        | 0         || String.format("'row' must be in range [0..5], but is '%s'.", row)
        "row>=board side" | 5         | boardSize | 0         || String.format("'row' must be in range [0..5], but is '%s'.", row)
        "col < 0"         | 5         | 0         | -1        || String.format("'col' must be in range [0..5], but is '%s'.", col)
        "col>=board side" | 5         | 0         | boardSize || String.format("'col' must be in range [0..5], but is '%s'.", col)
    }

    @Unroll
    def "must throw an exception when isEmptyCell(...) invoked with #testTitle"() {

        given:
        int holesNumber = 7
        Board initialBoard = Board.initWith(boardSize, holesNumber)

        when:
        initialBoard.isEmptyCell(row, col)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == expecetedMessage

        where:
        testTitle         | boardSize | row       | col       || expecetedMessage
        "row < 0"         | 5         | -1        | 0         || String.format("'row' must be in range [0..5], but is '%s'.", row)
        "row>=board side" | 5         | boardSize | 0         || String.format("'row' must be in range [0..5], but is '%s'.", row)
        "col < 0"         | 5         | 0         | -1        || String.format("'col' must be in range [0..5], but is '%s'.", col)
        "col>=board side" | 5         | 0         | boardSize || String.format("'col' must be in range [0..5], but is '%s'.", col)
    }

    @Unroll
    def "must throw an exception when isCellOpen(...) invoked with #testTitle"() {

        given:
        int holesNumber = 7
        Board initialBoard = Board.initWith(boardSize, holesNumber)

        when:
        initialBoard.isCellOpen(row, col)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == expecetedMessage

        where:
        testTitle         | boardSize | row       | col       || expecetedMessage
        "row < 0"         | 5         | -1        | 0         || String.format("'row' must be in range [0..5], but is '%s'.", row)
        "row>=board side" | 5         | boardSize | 0         || String.format("'row' must be in range [0..5], but is '%s'.", row)
        "col < 0"         | 5         | 0         | -1        || String.format("'col' must be in range [0..5], but is '%s'.", col)
        "col>=board side" | 5         | 0         | boardSize || String.format("'col' must be in range [0..5], but is '%s'.", col)
    }

    @Unroll
    def "must throw an exception when getCellContent(...) invoked with #testTitle"() {

        given:
        int holesNumber = 7
        Board initialBoard = Board.initWith(boardSize, holesNumber)

        when:
        initialBoard.getCellContent(row, col)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == expecetedMessage

        where:
        testTitle         | boardSize | row       | col       || expecetedMessage
        "row < 0"         | 5         | -1        | 0         || String.format("'row' must be in range [0..5], but is '%s'.", row)
        "row>=board side" | 5         | boardSize | 0         || String.format("'row' must be in range [0..5], but is '%s'.", row)
        "col < 0"         | 5         | 0         | -1        || String.format("'col' must be in range [0..5], but is '%s'.", col)
        "col>=board side" | 5         | 0         | boardSize || String.format("'col' must be in range [0..5], but is '%s'.", col)
    }
}
