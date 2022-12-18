import kotlin.math.abs

fun main(args: Array<String>) {
    println("Part 1: ${Day18.solvePart1()}")
    println("Part 2: ${Day18.solvePart2()}")
}

object Day18 {

    fun solvePart1(): Int {
        val input = Day18::class.java.getResource("day18.txt")?.readText() ?: error("Can't read input")
        val points = input.split("\r\n")
            .map { line ->
                val coords = line.split(",")
                Coordinate(coords[0].toInt(), coords[1].toInt(), coords[2].toInt())
            }
        var touchingCount = 0
        for (firstIdx in points.indices) {
            for (secondIdx in firstIdx + 1 until points.size) {
                if (points[firstIdx].touches(points[secondIdx])) {
                    touchingCount++
                }
            }
        }

        return points.size*6 - touchingCount*2
    }

    fun solvePart2(): Long {
        val input = Day18::class.java.getResource("day18.txt")?.readText() ?: error("Can't read input")
        return 1L
    }

    data class Coordinate(
        val x: Int,
        val y: Int,
        val z: Int,
    ) {
        fun touches(other: Coordinate): Boolean {
            return (this.x == other.x && this.y == other.y && abs(this.z - other.z) == 1) ||
                    (this.z == other.z && this.y == other.y && abs(this.x - other.x) == 1) ||
                    (this.x == other.x && this.z == other.z && abs(this.y - other.y) == 1)
        }
    }
}