import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
    println("Part 1: ${Day15.solvePart1()}")
    println("Part 2: ${Day15.solvePart2()}")
}

object Day15 {
    const val searchedRow = 2000000
    const val xMultiplier = 4000000
    const val minCoordinate = 0
    const val maxCoordinate = 4000000

    fun solvePart1(): Int {
        val input = Day15::class.java.getResource("day15.txt")?.readText() ?: error("Can't read input")
        val measurements = input.split("\r\n")
            .map { parseInput(it) }

        // fast solution has problems with covering other sensors/beacons
        val blockedCoordinates = measurements.flatMap { it.getBlockedCoordinatesInRowWithRange(searchedRow) }
        val mergedCoordinates = merge(blockedCoordinates)

        return mergedCoordinates.sumOf { it.size() }
    }

    private fun parseInput(it: String): Input {
        val sensorX = it.substringAfter("x=").substringBefore(",").toInt()
        val sensorY = it.substringAfter("y=").substringBefore(":").toInt()
        val beaconX = it.substringAfterLast("x=").substringBefore(",").toInt()
        val beaconY = it.substringAfterLast("y=").trim().toInt()
        return Input(
            Coordinate(sensorX, sensorY),
            Coordinate(beaconX, beaconY),
        )
    }

    fun solvePart2(): Long {
        val input = Day15::class.java.getResource("day15.txt")?.readText() ?: error("Can't read input")
        val measurements = input.split("\r\n")
            .map { parseInput(it) }

        val blockedPerRow = (minCoordinate..maxCoordinate).map { row ->
            merge(measurements.mapNotNull { it.getCoveredCoordinatesInRowWithRange(row, minCoordinate, maxCoordinate) })
        }

        val row = blockedPerRow.indexOfFirst { row -> row.size > 1 }
        val column = blockedPerRow[row].first().toInclusive + 1

        return column.toLong() * xMultiplier + row
    }

    fun merge(ranges: List<Range>): Set<Range> {
        val mergedRanges = mutableListOf<Range>()
        val sortedByStart = ranges.sortedBy { it.fromInclusive }
        var currentStart = sortedByStart.first().fromInclusive
        var currentEnd = sortedByStart.first().toInclusive
        for (range in sortedByStart.drop(1)) {
            if (range.fromInclusive > currentEnd + 1) {
                mergedRanges.add(Range(currentStart, currentEnd))
                currentStart = range.fromInclusive
                currentEnd = range.toInclusive
            } else {
                currentEnd = max(currentEnd, range.toInclusive)
            }
        }
        mergedRanges.add(Range(currentStart, currentEnd))
        return mergedRanges.toSet()
    }

    data class Input(
        val sensor: Coordinate,
        val beacon: Coordinate
    ) {
        // sloooow but works :D
        fun getBlockedCoordinatesInRow(wantedRow: Int): Set<Coordinate> {
            val coveredDistance = eukledianDistance(sensor, beacon)
            val coveredDistanceInRow = abs(coveredDistance - abs(sensor.y - wantedRow))
            val left = ((sensor.x - coveredDistanceInRow)..(sensor.x - 1)).map { Coordinate(it, wantedRow) }.toSet()
            val right = ((sensor.x + 1)..(sensor.x + coveredDistanceInRow)).map { Coordinate(it, wantedRow) }.toSet()
            return left + right
        }

        fun getBlockedCoordinatesInRowWithRange(wantedRow: Int): List<Range> {
            val coveredDistance = eukledianDistance(sensor, beacon)
            val coveredDistanceInRow = coveredDistance - abs(sensor.y - wantedRow)
            if (coveredDistanceInRow <= 0) {
                return emptyList()
            }
            val left = Range(sensor.x - coveredDistanceInRow, sensor.x - 1)
            val right = Range(sensor.x + 1, sensor.x + coveredDistanceInRow)
            return left.union(right)
        }

        fun getCoveredCoordinatesInRowWithRange(wantedRow: Int, minCoordinate: Int, maxCoordinate: Int): Range? {
            val coveredDistance = eukledianDistance(sensor, beacon)
            val coveredDistanceInRow = coveredDistance - abs(sensor.y - wantedRow)
            if (coveredDistanceInRow <= 0) {
                return null
            }
            val from = max(minCoordinate, sensor.x - coveredDistanceInRow)
            val to = min(maxCoordinate, sensor.x + coveredDistanceInRow)
            return Range(from, to)
        }

    }

    data class Range(
        val fromInclusive: Int,
        val toInclusive: Int,
    ) {
        fun union(other: Range): List<Range> {
            val minFrom = min(fromInclusive, other.fromInclusive)
            val maxFrom = max(fromInclusive, other.fromInclusive)
            val minTo = min(toInclusive, other.toInclusive)
            val maxTo = max(toInclusive, other.toInclusive)

            return if (maxFrom <= minTo + 1) {
                listOf(Range(minFrom, maxTo))
            } else {
                listOf(this, other)
            }
        }

        fun size(): Int =
            (toInclusive - fromInclusive) + 1
    }

    data class Coordinate(
        val x: Int,
        val y: Int,
    )

    private fun eukledianDistance(first: Coordinate, second: Coordinate): Int =
        abs(first.x - second.x) + abs(first.y - second.y)


}
