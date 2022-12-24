fun main(args: Array<String>) {
    println("Part 1: ${Day24.solvePart1()}")
    println("Part 2: ${Day24.solvePart2()}")
}

object Day24 {
    const val OPEN = "."
    const val WALL = "#"
    const val BLIZZARD_RIGHT = ">"
    const val BLIZZARD_LEFT = "<"
    const val BLIZZARD_DOWN = "v"
    const val BLIZZARD_UP = "^"
    var SHORTEST_PATH = Int.MAX_VALUE

    fun solvePart1(): Int {
        val input = Day24::class.java.getResource("day24.txt")?.readText() ?: error("Can't read input")
        val cave = input.split("\r\n")
            .map { line ->
                line.split("").filter { it.isNotBlank() }.map {
                    if (it == OPEN) {
                        CaveField(mutableListOf())
                    } else {
                        CaveField(mutableListOf(it))
                    }
                }
            }
        val startX = cave.first().indexOfFirst { it.elements.isEmpty() }
        val goalX = cave.last().indexOfLast { it.elements.isEmpty() }
        val startPosition = Position(0, startX)
        val goalPosition = Position(cave.size - 1, goalX)
        SHORTEST_PATH = Int.MAX_VALUE

        val firstRunToGoal = move(listOf(Path(startPosition, listOf(startPosition), 0)), cave, goalPosition)

        return firstRunToGoal.minOf { it.minutesPassed }
    }

    fun move(possiblePaths: List<Path>, cave: List<List<CaveField>>, goal: Position): List<Path> {
        val newCave = moveBlizzards(cave)
       // if (possiblePaths.isNotEmpty()) {
       //     val minutes = possiblePaths.map { it.minutesPassed }.first()
       //     println("Cave after ${minutes + 1} minutes")
       //     println(printCave(newCave))
       // }

        val (alreadyFinishedPaths, notYetFinishedPaths) = possiblePaths
            .filter { it.minutesPassed < SHORTEST_PATH }
            .partition { it.current == goal }

        if (alreadyFinishedPaths.isNotEmpty()) {
            val newMin = alreadyFinishedPaths.minOf { it.minutesPassed }
            if (newMin < SHORTEST_PATH) {
                SHORTEST_PATH = newMin
            }
            return alreadyFinishedPaths
        }

        val allPaths = notYetFinishedPaths.flatMap { path ->
            val options = mutableListOf<Path>()
            if (newCave[path.current.row][path.current.col].elements.isEmpty()) {
                options.add(path.copy(minutesPassed = path.minutesPassed + 1))
            }
            val down = Position(path.current.row + 1, path.current.col)
            if (canMoveTo(newCave, down)) {
                options.add(Path(down, path.path + listOf(down), path.minutesPassed + 1))
            }
            val up = Position(path.current.row - 1, path.current.col)
            if (canMoveTo(newCave, up)) {
                options.add(Path(up, path.path + listOf(up), path.minutesPassed + 1))
            }
            val right = Position(path.current.row, path.current.col + 1)
            if (canMoveTo(newCave, right)) {
                options.add(Path(right, path.path + listOf(right), path.minutesPassed + 1))
            }
            val left = Position(path.current.row, path.current.col - 1)
            if (canMoveTo(newCave, left)) {
                options.add(Path(left, path.path + listOf(left), path.minutesPassed + 1))
            }
            options.toList()
        }

        val distinctByPosition = reduce(allPaths)
        return move(distinctByPosition, newCave, goal)
    }

    private fun reduce(paths: List<Path>): List<Path> {
        return paths.groupBy { it.current }
            .map { it.value.minBy { it.minutesPassed } }
    }

    private fun printCave(newCave: List<List<CaveField>>) =
        newCave.joinToString("\n") {
            it.joinToString("") { e ->
                if (e.elements.isEmpty()) {
                    "."
                } else if (e.elements.size == 1) {
                    e.elements[0]
                } else {
                    e.elements.size.toString()
                }
            }
        }

    fun canMoveTo(cave: List<List<CaveField>>, next: Position): Boolean {
        return next.row >= 0 && next.col >= 0 && next.row < cave.size && next.col < cave.first().size && cave[next.row][next.col].elements.isEmpty()
    }

    fun moveBlizzards(inital: List<List<CaveField>>, times: Int): List<List<CaveField>> {
        var cave = inital
        repeat(times) {
            cave = moveBlizzards(cave)
        }
        return cave
    }

    fun moveBlizzards(cave: List<List<CaveField>>): List<List<CaveField>> {
        val newCave = cave.map {
            it.map {
                CaveField(it.elements.filter { el ->
                    el !in listOf(
                        BLIZZARD_DOWN,
                        BLIZZARD_UP,
                        BLIZZARD_LEFT,
                        BLIZZARD_RIGHT
                    )
                }.toMutableList())
            }
        }

        val lastFreeRow = newCave.size - 2
        val lastFreeColumn = newCave.first().size - 2

        for (row in (1..lastFreeRow)) {
            for (col in (1..lastFreeColumn)) {
                val current = cave[row][col].elements
                if (current.contains(BLIZZARD_DOWN)) {
                    if (!cave[row + 1][col].elements.contains(WALL)) {
                        newCave[row + 1][col].elements.add(BLIZZARD_DOWN)
                    } else {
                        newCave[1][col].elements.add(BLIZZARD_DOWN)
                    }
                }
                if (current.contains(BLIZZARD_LEFT)) {
                    if (!cave[row][col - 1].elements.contains(WALL)) {
                        newCave[row][col - 1].elements.add(BLIZZARD_LEFT)
                    } else {
                        newCave[row][lastFreeColumn].elements.add(BLIZZARD_LEFT)
                    }
                }
                if (current.contains(BLIZZARD_RIGHT)) {
                    if (!cave[row][col + 1].elements.contains(WALL)) {
                        newCave[row][col + 1].elements.add(BLIZZARD_RIGHT)
                    } else {
                        newCave[row][1].elements.add(BLIZZARD_RIGHT)
                    }
                }
                if (current.contains(BLIZZARD_UP)) {
                    if (!cave[row - 1][col].elements.contains(WALL)) {
                        newCave[row - 1][col].elements.add(BLIZZARD_UP)
                    } else {
                        newCave[lastFreeRow][col].elements.add(BLIZZARD_UP)
                    }
                }
            }
        }

        //println(printCave(cave))
        //println()
        //println("becomes")
        //println()
        //println(printCave(newCave))

        return newCave
    }

    fun solvePart2(): Int {
        val input = Day24::class.java.getResource("day24.txt")?.readText() ?: error("Can't read input")
        val cave = input.split("\r\n")
            .map { line ->
                line.split("").filter { it.isNotBlank() }.map {
                    if (it == OPEN) {
                        CaveField(mutableListOf())
                    } else {
                        CaveField(mutableListOf(it))
                    }
                }
            }
        val startX = cave.first().indexOfFirst { it.elements.isEmpty() }
        val goalX = cave.last().indexOfLast { it.elements.isEmpty() }
        val startPosition = Position(0, startX)
        val goalPosition = Position(cave.size - 1, goalX)
        SHORTEST_PATH = Int.MAX_VALUE

        val firstRunToGoal = move(listOf(Path(startPosition, listOf(startPosition), 0)), cave, goalPosition)
        val shortestPathToGoal = firstRunToGoal.minBy { it.minutesPassed }
        val caveAfterFirstGoalVisit = moveBlizzards(cave, shortestPathToGoal.minutesPassed)
        //println(printCave(caveAfterFirstGoalVisit))
        SHORTEST_PATH = Int.MAX_VALUE
        val backToStart = move(listOf(shortestPathToGoal), caveAfterFirstGoalVisit, startPosition)
        val shortestPathToStart = backToStart.minBy { it.minutesPassed }
        val caveAfterBackToStart = moveBlizzards(cave, shortestPathToStart.minutesPassed)
        SHORTEST_PATH = Int.MAX_VALUE
        val secondRunToGoal = move(listOf(shortestPathToStart), caveAfterBackToStart, goalPosition)

        return secondRunToGoal.minOf { it.minutesPassed }
    }

    data class CaveField(
        val elements: MutableList<String>
    )

    data class Position(
        val row: Int,
        val col: Int
    )

    data class Path(
        val current: Position,
        val path: List<Position>,
        val minutesPassed: Int
    )
}