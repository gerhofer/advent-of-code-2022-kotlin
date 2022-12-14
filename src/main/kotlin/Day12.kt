fun main(args: Array<String>) {
    //println("Part 1: ${Day12.solvePart1()}")
    println("Part 2: ${Day12.solvePart2()}")
}

object Day12 {
    fun solvePart1(): Int {
        val input = Day12::class.java.getResource("day12.txt")?.readText() ?: error("Can't read input")
        val map = readMap(input)
        val numberOfRows = map.size
        val numberOfColumns = map[0].size
        val startPosition = map.flatten().first { it.isStart() }

        performAllPossibleMoves(
            startPosition.row,
            map,
            startPosition.col,
            startPosition,
            numberOfColumns,
            numberOfRows
        )

        return map.flatten().first { it.isEnd() }.shortestPath.size
    }

    private fun performAllPossibleMoves(
        rowIdx: Int,
        map: List<List<Position>>,
        colIdx: Int,
        currentPosition: Position,
        numberOfColumns: Int,
        numberOfRows: Int,
    ) {
        // UP
        if (rowIdx > 0) {
            val upwards = map[rowIdx - 1][colIdx]
            if (isReachable(upwards, currentPosition)) {
                if (isShorterThanExistingPath(upwards, currentPosition)) {
                    //println("Found new shortest path to ${rowIdx - 1} | ${colIdx} via UP")
                    upwards.shortestPath.clear()
                    upwards.shortestPath.addAll(currentPosition.shortestPath)
                    upwards.shortestPath.add(currentPosition)
                    performAllPossibleMoves(rowIdx - 1, map, colIdx, upwards, numberOfColumns, numberOfRows)
                }
            }
        }

        // RIGHT
        if (colIdx < numberOfColumns - 1) {
            val rightwards = map[rowIdx][colIdx + 1]
            if (isReachable(rightwards, currentPosition)) {
                if (isShorterThanExistingPath(rightwards, currentPosition)) {
                    //println("Found new shortest path to ${rowIdx} | ${colIdx + 1} via RIGHT")
                    rightwards.shortestPath.clear()
                    rightwards.shortestPath.addAll(currentPosition.shortestPath)
                    rightwards.shortestPath.add(currentPosition)
                    performAllPossibleMoves(rowIdx, map, colIdx + 1, rightwards, numberOfColumns, numberOfRows)
                }
            }
        }

        // DOWN
        if (rowIdx < numberOfRows - 1) {
            val downwards = map[rowIdx + 1][colIdx]
            if (isReachable(downwards, currentPosition)) {
                if (isShorterThanExistingPath(downwards, currentPosition)) {
                    //println("Found new shortest path to ${rowIdx + 1} | ${colIdx} via DOWN")
                    downwards.shortestPath.clear()
                    downwards.shortestPath.addAll(currentPosition.shortestPath)
                    downwards.shortestPath.add(currentPosition)
                    performAllPossibleMoves(rowIdx + 1, map, colIdx, downwards, numberOfColumns, numberOfRows)
                }
            }
        }

        // LEFT
        if (colIdx > 0) {
            val leftwards = map[rowIdx][colIdx - 1]
            if (isReachable(leftwards, currentPosition)) {
                if (isShorterThanExistingPath(leftwards, currentPosition)) {
                    //println("Found new shortest path to ${rowIdx} | ${colIdx - 1} via LEFT")
                    leftwards.shortestPath.clear()
                    leftwards.shortestPath.addAll(currentPosition.shortestPath)
                    leftwards.shortestPath.add(currentPosition)
                    performAllPossibleMoves(rowIdx, map, colIdx - 1, leftwards, numberOfColumns, numberOfRows)
                }
            }
        }

    }

    private fun isShorterThanExistingPath(goal: Position, current: Position) =
        (goal.shortestPath.isEmpty()) || (current.shortestPath.size + 1) < goal.shortestPath.size

    private fun isReachable(goal: Position, current: Position) =
        goal.value() <= current.value() + 1 && !current.shortestPath.contains(goal)

    fun solvePart2(): Int {
        val input = Day12::class.java.getResource("day12.txt")?.readText() ?: error("Can't read input")
        val map = readMap(input)
        val numberOfRows = map.size
        val numberOfColumns = map[0].size
        val startPositions = map.flatten().filter { it.isStart() || it.letter == 'a' }

        val shortestPaths = startPositions.map {
            val shortesPath = getShortestPath(it, readMap(input), numberOfColumns, numberOfRows)
            println("checking for starting at ${it.row} ${it.col} is $shortesPath")
            shortesPath
        }

        return shortestPaths.filter { it > 0 }.min()
    }

    private fun readMap(input: String): List<List<Position>> {
        val map = input.split("\r\n")
            .mapIndexed { rowNr, it ->
                it.split("")
                    .filter { char -> char.isNotBlank() }
                    .mapIndexed { colNr, char -> Position(rowNr, colNr, char.first(), mutableListOf()) }
            }
        return map
    }

    private fun getShortestPath(
        startPosition: Position,
        map: List<List<Position>>,
        numberOfColumns: Int,
        numberOfRows: Int
    ): Int {
        performAllPossibleMoves(
            startPosition.row,
            map,
            startPosition.col,
            startPosition,
            numberOfColumns,
            numberOfRows
        )

        return map.flatten().first { it.isEnd() }.shortestPath.size
    }

    data class Position(
        val row: Int,
        val col: Int,
        val letter: Char,
        val shortestPath: MutableList<Position>,
    ) {

        fun isStart(): Boolean =
            letter == 'S'

        fun isEnd(): Boolean =
            letter == 'E'

        fun value(): Int =
            if (letter.isLowerCase()) {
                letter.code
            } else if (isStart()) {
                'a'.code
            } else if (isEnd()) {
                'z'.code
            } else {
                error("Found letter that is neither a lowercase letter nor S or E")
            }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Position

            if (row != other.row) return false
            if (col != other.col) return false

            return true
        }

        override fun hashCode(): Int {
            var result = row
            result = 31 * result + col
            return result
        }
    }

}
