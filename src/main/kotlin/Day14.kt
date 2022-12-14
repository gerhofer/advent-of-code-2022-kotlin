import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
    println("Part 1: ${Day14.solvePart1()}")
    println("Part 2: ${Day14.solvePart2()}")
}

object Day14 {
    val startingPoint = Coordinate(500, 0)

    fun solvePart1(): Int {
        val input = Day14::class.java.getResource("day14.txt")?.readText() ?: error("Can't read input")
        val lines = readCoordinates(input)
        val bottomLine = lines.flatten().maxOf { it.y }
        val points = generatePointsForLines(lines)
        var sandCount = 0
        var current = startingPoint
        var reachedBottom = false
        while (!reachedBottom) {
            var newCoordinate = moveDown(current, points)
            while (newCoordinate != null) {
                if (newCoordinate.y >= bottomLine) {
                    reachedBottom = true
                    break
                }
                current = newCoordinate
                newCoordinate = moveDown(current, points)
            }
            //println("Adding $current, sandcount is $sandCount")
            points.add(current)
            current = startingPoint
            sandCount++
        }

        return sandCount - 1
    }

    private fun moveDown(current: Coordinate, points: MutableSet<Coordinate>, floorY: Int? = null): Coordinate? {
        if (floorY != null && current.y == floorY) {
            return null
        }
        val down = Coordinate(current.x, current.y + 1)
        val leftDown = Coordinate(current.x - 1, current.y + 1)
        val rightDown = Coordinate(current.x + 1, current.y + 1)
        if (!points.contains(down)) {
            return down
        } else if (!points.contains(leftDown)) {
            return leftDown
        } else if (!points.contains(rightDown)) {
            return rightDown
        }
        return null
    }

    private fun generatePointsForLines(lines: List<List<Coordinate>>): MutableSet<Coordinate> {
        return lines.flatMap {
            generatePoints(it)
        }.toMutableSet()
    }

    private fun generatePoints(line: List<Coordinate>): MutableSet<Coordinate> {
        val pointsOnLine = mutableSetOf<Coordinate>()
        for (i in 1 until line.size) {
            val from = line[i - 1]
            val to = line[i]
            if (from.x == to.x) {
                (min(from.y, to.y)..max(from.y, to.y)).forEach {
                    pointsOnLine.add(Coordinate(from.x, it))
                }
            } else if (from.y == to.y) {
                (min(from.x, to.x)..max(from.x, to.x)).forEach {
                    pointsOnLine.add(Coordinate(it, from.y))
                }
            }
        }
        return pointsOnLine
    }

    private fun readCoordinates(input: String) = input.split("\r\n")
        .map {
            it.split(" -> ")
                .filter { coordinate -> coordinate.isNotBlank() }
                .map { coordinate ->
                    Coordinate(
                        coordinate.substringBefore(",").trim().toInt(),
                        coordinate.substringAfter(",").trim().toInt()
                    )
                }
        }

    fun solvePart2(): Int {
        val input = Day14::class.java.getResource("day14.txt")?.readText() ?: error("Can't read input")
        val lines = readCoordinates(input)
        val bottomLine = lines.flatten().maxOf { it.y }
        val points = generatePointsForLines(lines)
        var sandCount = 0
        var current = startingPoint
        while (!points.contains(startingPoint)) {
            var newCoordinate = moveDown(current, points, bottomLine + 1)
            while (newCoordinate != null) {
                current = newCoordinate
                newCoordinate = moveDown(current, points, bottomLine + 1)
            }
            //println("Adding $current, sandcount is $sandCount")
            points.add(current)
            current = startingPoint
            sandCount++
        }

        return sandCount
    }

    data class Coordinate(
        val x: Int,
        val y: Int,
    )

}
