fun main(args: Array<String>) {
    println("Part 1: ${Day17.solvePart1()}")
    println("Part 2: ${Day17.solvePart2()}")
}

object Day17 {

    val LINE_HORIZONTAL = RockPattern(
        listOf(Coordinate(0, 0), Coordinate(0, 1), Coordinate(0, 2), Coordinate(0, 3))
    )
    val PLUS = RockPattern(
        listOf(Coordinate(0, 1), Coordinate(1, 0), Coordinate(1, 1), Coordinate(1, 2), Coordinate(2, 1))
    )
    val L = RockPattern(
        listOf(Coordinate(0, 0), Coordinate(0, 1), Coordinate(0, 2), Coordinate(1, 2), Coordinate(2, 2))
    )
    val LINE_VERTICAL = RockPattern(
        listOf(Coordinate(0, 0), Coordinate(1, 0), Coordinate(2, 0), Coordinate(3, 0))
    )
    val CUBE = RockPattern(
        listOf(Coordinate(0, 0), Coordinate(0, 1), Coordinate(1, 0), Coordinate(1, 1))
    )
    val rockPatterns = listOf(LINE_HORIZONTAL, PLUS, L, LINE_VERTICAL, CUBE)
    val startingXOffset = 4
    val startingColumn = 2
    val numberOfRocksToFall = 2022

    fun solvePart1(): Int {
        val input = Day17::class.java.getResource("day17.txt")?.readText() ?: error("Can't read input")
        val jetPattern = input.split("").filter { it.isNotBlank() }
        val cave = mutableMapOf<Int, MutableSet<Int>>() // row to filledParts
        cave[-1] = mutableSetOf(0, 1, 2, 3, 4, 5, 6)
        var rockCount = 0
        var idxInJetPattern = 0
        var currentRock: Rock? = null

        while (rockCount < numberOfRocksToFall) {
            // needs new rock?
            if (currentRock == null) {
                val startingRow = (cave.keys.maxOrNull() ?: 0) + startingXOffset
                currentRock = Rock(Coordinate(startingRow, startingColumn), rockPatterns[rockCount % rockPatterns.size])
            }

            // jet movement
            if (idxInJetPattern == jetPattern.size) {
                idxInJetPattern = 0
            }
            if (jetPattern[idxInJetPattern] == ">") {
                if (currentRock.canMoveRight() && canMoveRight(cave, currentRock)) {
                    currentRock.position.moveRight()
                }
            } else {
                if (currentRock.canMoveLeft() && canMoveLeft(cave, currentRock)) {
                    currentRock.position.moveLeft()
                }
            }
            idxInJetPattern++

            // down movement / coming to rest
            if (canMoveDown(cave, currentRock)) {
                currentRock.position.moveDown()
            } else {
                val positionsInCaveByRow = currentRock.getAbsolutePositions().groupBy { it.row }
                for (positionsInRow in positionsInCaveByRow) {
                    val stonesInRow = cave.getOrDefault(positionsInRow.key, mutableSetOf())
                    stonesInRow.addAll(positionsInRow.value.map { it.column })
                    cave[positionsInRow.key] = stonesInRow
                }
                //drawCave(cave)
                currentRock = null
                rockCount++
            }
        }

        return cave.keys.max() + 1
    }

    fun drawCave(cave: Map<Int, Set<Int>>) {
        val printed = cave.map {
            (0..6).joinToString("") { c ->
                if (it.value.contains(c)) {
                    "#"
                } else {
                    "."
                }
            }
        }.reversed().joinToString("\r\n")
        println(printed)
        println()
    }

    fun solvePart2(): Long {
        val input = Day17::class.java.getResource("day17.txt")?.readText() ?: error("Can't read input")
        return 1L
    }

    private fun canMoveDown(cave: Map<Int, Set<Int>>, rock: Rock): Boolean {
        val positionsInCave = rock.getAbsolutePositions()
        val posPerRow = positionsInCave.groupBy { it.row }
        return posPerRow.all {
            cave.getOrDefault(it.key - 1, emptySet()).intersect(it.value.map { v -> v.column }.toSet()).isEmpty()
        }
    }

    private fun canMoveLeft(cave: Map<Int, Set<Int>>, rock: Rock): Boolean {
        val positionsInCave = rock.getAbsolutePositions()
        val posPerRow = positionsInCave.groupBy { it.row }
        return posPerRow.all {
            cave.getOrDefault(it.key, emptySet()).intersect(it.value.map { v -> v.column - 1 }.toSet()).isEmpty()
        }
    }

    private fun canMoveRight(cave: Map<Int, Set<Int>>, rock: Rock): Boolean {
        val positionsInCave = rock.getAbsolutePositions()
        val posPerRow = positionsInCave.groupBy { it.row }
        return posPerRow.all {
            cave.getOrDefault(it.key, emptySet()).intersect(it.value.map { v -> v.column + 1 }.toSet()).isEmpty()
        }
    }

    val MIN_COLUMN = 0
    val MAX_COLUMN = 6

    data class Rock(
        var position: Coordinate,
        val pattern: RockPattern
    ) {
        fun getAbsolutePositions(): List<Coordinate> {
            return pattern.relativeToMiddle.map { Coordinate(it.row + position.row, it.column + position.column) }
        }

        fun canMoveLeft(): Boolean {
            val leftEdge = pattern.relativeToMiddle.minOf { it.column + position.column }
            return leftEdge > MIN_COLUMN
        }

        fun canMoveRight(): Boolean {
            val rightEdge = pattern.relativeToMiddle.maxOf { it.column + position.column }
            return rightEdge < MAX_COLUMN
        }
    }

    data class RockPattern(
        val relativeToMiddle: List<Coordinate>
    )

    data class Coordinate(
        var row: Int,
        var column: Int
    ) {
        fun moveLeft() {
            this.column--
        }

        fun moveRight() {
            this.column++
        }

        fun moveDown() {
            this.row--
        }
    }
}