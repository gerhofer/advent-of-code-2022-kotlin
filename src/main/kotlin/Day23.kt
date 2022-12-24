fun main(args: Array<String>) {
    println("Part 1: ${Day23.solvePart1()}")
    println("Part 2: ${Day23.solvePart2()}")
}

object Day23 {
    val directions = listOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)

    fun solvePart1(): Int {
        val input = Day23::class.java.getResource("day23.txt")?.readText() ?: error("Can't read input")
        val elves = parseElves(input)

        var movingElves = elves
       // print(movingElves)
        repeat(10) { index ->
           // println("Round $index trying ${directions[index % 4]} first")
            val proposedPositions = movingElves.map {
                move(index, movingElves, it)
            }
            val newElves = proposedPositions.map { elve ->
                if (proposedPositions.count { other -> other.row == elve.row && other.column == elve.column } == 1) {
                    elve
                } else {
                    movingElves.first { it.id == elve.id }
                }
            }
            movingElves = newElves
            //print(movingElves)
        }

        return countFreeSpace(movingElves)
    }

    private fun parseElves(input: String): List<Position> {
        var elveCount = 0
        val elves = input.split("\r\n")
            .map { line -> line.split("").filter { it.isNotBlank() } }
            .mapIndexed { row, elves ->
                elves.mapIndexed { col, elve ->
                    if (elve == "#") {
                        Position(row, col, elveCount++)
                    } else {
                        null
                    }
                }
            }
            .flatten()
            .filterNotNull()
        return elves
    }

    fun print(movingElves: List<Position>) {
        val minRow = movingElves.minOf { it.row }
        val maxRow = movingElves.maxOf { it.row }
        val minColumn = movingElves.minOf { it.column }
        val maxColumn = movingElves.maxOf { it.column }

        val asString = (minRow..maxRow).map { row ->
            (minColumn..maxColumn).map { col ->
                if (movingElves.none { it.row == row && it.column == col }) {
                    "."
                } else {
                    "#"
                }
            }.joinToString("")
        }.joinToString("\r\n")

        println()
        println(asString)
    }

    fun countFreeSpace(movingElves: List<Position>): Int {
        val minRow = movingElves.minOf { it.row }
        val maxRow = movingElves.maxOf { it.row }
        val minColumn = movingElves.minOf { it.column }
        val maxColumn = movingElves.maxOf { it.column }

        var count = 0
        for (row in minRow..maxRow) {
            for (col in minColumn..maxColumn) {
                if (movingElves.none { it.row == row && it.column == col }) {
                    count++
                }
            }
        }
        return count
    }

    fun move(directionStartIndex: Int, allElves: List<Position>, elve: Position): Position {
        val neighbourCount =
            allElves.count { it.column in (elve.column - 1..elve.column + 1) && it.row in (elve.row - 1..elve.row + 1) }
        if (neighbourCount > 1) {
            repeat(4) { idx ->
                val newPosition = move(directions[(directionStartIndex + idx) % 4], allElves, elve)
                if (newPosition != null) {
                    //println("Elve id ${elve.id} moving to ${newPosition.row} | ${newPosition.column} (direction was ${directions[(directionStartIndex + idx) % 4]})")
                    return newPosition
                }
            }
        }
        return elve
    }

    fun move(direction: Direction, allElves: List<Position>, elve: Position): Position? {
        return when (direction) {
            Direction.NORTH -> {
                if (allElves.none { it.row == (elve.row - 1) && it.column in (elve.column - 1..elve.column + 1) }) {
                    Position(elve.row - 1, elve.column, elve.id)
                } else {
                    null
                }
            }

            Direction.SOUTH -> {
                if (allElves.none { it.row == (elve.row + 1) && it.column in (elve.column - 1..elve.column + 1) }) {
                    Position(elve.row + 1, elve.column, elve.id)
                } else {
                    null
                }
            }

            Direction.WEST -> {
                if (allElves.none { it.row in (elve.row - 1..elve.row + 1) && it.column == elve.column - 1 }) {
                    Position(elve.row, elve.column - 1, elve.id)
                } else {
                    null
                }
            }

            Direction.EAST -> {
                if (allElves.none { it.row in (elve.row - 1..elve.row + 1) && it.column == elve.column + 1 }) {
                    Position(elve.row, elve.column + 1, elve.id)
                } else {
                    null
                }
            }
        }
    }

    fun solvePart2(): Int {
        val input = Day23::class.java.getResource("day23.txt")?.readText() ?: error("Can't read input")
        val elves = parseElves(input)

        var movingElves = elves
        var someMoved = true
        var roundCount = 0
        while(someMoved) {
            // println("Round $index trying ${directions[index % 4]} first")
            val proposedPositions = movingElves.map {
                move(roundCount, movingElves, it)
            }
            val newElves = proposedPositions.map { elve ->
                if (proposedPositions.count { other -> other.row == elve.row && other.column == elve.column } == 1) {
                    elve
                } else {
                    movingElves.first { it.id == elve.id }
                }
            }
            someMoved = newElves.toSet().subtract(movingElves.toSet()).isNotEmpty()
            movingElves = newElves
            roundCount++
            //print(movingElves)
        }

        return roundCount
    }


    enum class Direction {
        NORTH, EAST, SOUTH, WEST
    }

    data class Position(
        val row: Int,
        val column: Int,
        val id: Int
    )
}