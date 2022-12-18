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

    fun solvePart1(): Long {
        val input = Day17::class.java.getResource("day17.txt")?.readText() ?: error("Can't read input")
        val jetPattern = input.split("").filter { it.isNotBlank() }
        val cave = mutableMapOf<Long, MutableSet<Int>>() // row to filledParts
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

        return cave.keys.max() + 1L
    }

    fun getDecidingPattern(cave: Map<Long, Set<Int>>): Map<Long, Set<Int>> {
        val maxRowPerColumn = (0..6).map { col -> cave.filter { it.value.contains(col) }.maxOfOrNull { it.key } ?: 0L }
        return cave.filter { it.key >= maxRowPerColumn.min() }
    }

    fun drawCave(cave: Map<Long, Set<Int>>) {
        val printed = asString(cave)
        println(printed)
        println()
    }

    private fun asString(cave: Map<Long, Set<Int>>): String {
        val printed = cave.map {
            (0..6).joinToString("") { c ->
                if (it.value.contains(c)) {
                    "#"
                } else {
                    "."
                }
            }
        }.reversed().joinToString("\r\n")
        return printed
    }

    fun solvePart2(): Long {
        val input = Day17::class.java.getResource("day17.txt")?.readText() ?: error("Can't read input")
        val jetPattern = input.split("").filter { it.isNotBlank() }
        val cave = mutableMapOf<Long, MutableSet<Int>>() // row to filledParts
        cave[-1] = mutableSetOf(0, 1, 2, 3, 4, 5, 6)
        var rockCount = 0L
        var idxInJetPattern = 0
        var currentRock: Rock? = null
        val seenStates = mutableSetOf<State>()
        var patternFits = 0L
        var heightOfPattern = 0L
        val wantedNumberOfRocks = 1000000000000
        var heightTilFirstPattern = 0L

        while (rockCount < wantedNumberOfRocks) {
            // needs new rock?
            if (currentRock == null) {
                val startingRow = (cave.keys.maxOrNull() ?: 0L) + startingXOffset
                currentRock =
                    Rock(Coordinate(startingRow, startingColumn), rockPatterns[(rockCount % rockPatterns.size).toInt()])
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
                currentRock = null
                rockCount++

                val state = State(idxInJetPattern, rockCount, getDecidingPattern(cave))
                if (seenStates.contains(state) && heightOfPattern == 0L) {
                    val existingState = seenStates.first { it == state }
                    val heightAtPreviousSimilarState = existingState.getHeightAtTime()
                    val heightNow = state.getHeightAtTime()
                    heightOfPattern = heightNow - heightAtPreviousSimilarState
                    heightTilFirstPattern = heightAtPreviousSimilarState

                    val rockCountAtPreviousSimilarState = existingState.rockCount
                    val rockCountNow = state.rockCount
                    val rocksPerPattern = rockCountNow - rockCountAtPreviousSimilarState

                    patternFits = (wantedNumberOfRocks - rockCount) / rocksPerPattern
                    rockCount = rockCount + patternFits * rocksPerPattern
                    println("Continue at ${rockCount} with a height of ${(patternFits + 1) * heightOfPattern}")
                }
                seenStates.add(state)
            }
        }

        val offset = cave.keys.max() - (heightOfPattern)
        return (patternFits + 1)* heightOfPattern + (offset + 1)
    }

    private fun canMoveDown(cave: Map<Long, Set<Int>>, rock: Rock): Boolean {
        val positionsInCave = rock.getAbsolutePositions()
        val posPerRow = positionsInCave.groupBy { it.row }
        return posPerRow.all {
            cave.getOrDefault(it.key - 1, emptySet()).intersect(it.value.map { v -> v.column }.toSet()).isEmpty()
        }
    }

    private fun canMoveLeft(cave: Map<Long, Set<Int>>, rock: Rock): Boolean {
        val positionsInCave = rock.getAbsolutePositions()
        val posPerRow = positionsInCave.groupBy { it.row }
        return posPerRow.all {
            cave.getOrDefault(it.key, emptySet()).intersect(it.value.map { v -> v.column - 1 }.toSet()).isEmpty()
        }
    }

    private fun canMoveRight(cave: Map<Long, Set<Int>>, rock: Rock): Boolean {
        val positionsInCave = rock.getAbsolutePositions()
        val posPerRow = positionsInCave.groupBy { it.row }
        return posPerRow.all {
            cave.getOrDefault(it.key, emptySet()).intersect(it.value.map { v -> v.column + 1 }.toSet()).isEmpty()
        }
    }

    val MIN_COLUMN = 0
    val MAX_COLUMN = 6

    data class State(
        val jetIndex: Int,
        val rockCount: Long,
        val linesUntilAllIndices: Map<Long, Set<Int>>,
    ) {

        fun getHeightAtTime() = linesUntilAllIndices.keys.max()
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as State

            if (jetIndex != other.jetIndex) return false
            if ((rockCount % rockPatterns.size).toInt() != (other.rockCount % rockPatterns.size).toInt()) return false
            if (asString(linesUntilAllIndices) != asString(other.linesUntilAllIndices)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = jetIndex
            result = 31 * result + (rockCount % rockPatterns.size).toInt()
            result = 31 * result + asString(linesUntilAllIndices).hashCode()
            return result
        }
    }

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
        var row: Long,
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