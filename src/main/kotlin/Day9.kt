import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
    println("Part 1: ${Day9.solvePart1()}")
    println("Part 2: ${Day9.solvePart2()}")
}

object Day9 {

    fun solvePart1(): Int {
        val input = Day202020::class.java.getResource("day9.txt")?.readText() ?: error("Can't read input")
        var headPosition = Position(0, 0)
        var tailPosition = Position(0, 0)
        val visitedTailPositions = mutableListOf(tailPosition)
        val movements = input.split("\r\n")
            .map { line ->
                val parts = line.split(" ")
                Movement(parts.first(), parts.last().toInt())
            }

        for (movement in movements) {
            repeat(movement.amount) {
                when (movement.direction) {
                    "R" -> headPosition = Position(headPosition.x + 1, headPosition.y)
                    "L" -> headPosition = Position(headPosition.x - 1, headPosition.y)
                    "U" -> headPosition = Position(headPosition.x, headPosition.y + 1)
                    "D" -> headPosition = Position(headPosition.x, headPosition.y - 1)
                }
                tailPosition = moveTailToHead(headPosition, tailPosition)
                //println("Head is at $headPosition, tail is at $tailPosition")
                visitedTailPositions.add(tailPosition)
            }
        }

        return visitedTailPositions.distinct().size
    }

    fun moveTailToHead(headPosition: Position, tailPosition: Position): Position {
        if (!headPosition.touches(tailPosition)) {
            val xDifference = headPosition.x - tailPosition.x
            val yDifference = headPosition.y - tailPosition.y
            val newTailX = if (xDifference >= 1) {
                // tail moves right
                tailPosition.x + 1
            } else if (xDifference <= -1){
                // tail moves left
                tailPosition.x - 1
            } else {
                tailPosition.x
            }

            val newTailY = if (yDifference >= 1) {
                // tail moves up
                tailPosition.y + 1
            } else if (yDifference <= -1){
                // tail moves down
                tailPosition.y - 1
            } else {
                tailPosition.y
            }
            return Position(newTailX, newTailY)
        } else {
            return tailPosition
        }
    }

    fun solvePart2(): Int {
        val input = Day202020::class.java.getResource("day9.txt")?.readText() ?: error("Can't read input")
        var headPosition = Position(0, 0)
        var tail1Position = Position(0, 0)
        var tail2Position = Position(0, 0)
        var tail3Position = Position(0, 0)
        var tail4Position = Position(0, 0)
        var tail5Position = Position(0, 0)
        var tail6Position = Position(0, 0)
        var tail7Position = Position(0, 0)
        var tail8Position = Position(0, 0)
        var tail9Position = Position(0, 0)
        var tailPosition = Position(0, 0)
        val visitedTailPositions = mutableListOf(tailPosition)
        val movements = input.split("\r\n")
            .map { line ->
                val parts = line.split(" ")
                Movement(parts.first(), parts.last().toInt())
            }

        for (movement in movements) {
            repeat(movement.amount) {
                when (movement.direction) {
                    "R" -> headPosition = Position(headPosition.x + 1, headPosition.y)
                    "L" -> headPosition = Position(headPosition.x - 1, headPosition.y)
                    "U" -> headPosition = Position(headPosition.x, headPosition.y + 1)
                    "D" -> headPosition = Position(headPosition.x, headPosition.y - 1)
                }
                tail1Position = moveTailToHead(headPosition, tail1Position)
                tail2Position = moveTailToHead(tail1Position, tail2Position)
                tail3Position = moveTailToHead(tail2Position, tail3Position)
                tail4Position = moveTailToHead(tail3Position, tail4Position)
                tail5Position = moveTailToHead(tail4Position, tail5Position)
                tail6Position = moveTailToHead(tail5Position, tail6Position)
                tail7Position = moveTailToHead(tail6Position, tail7Position)
                tail8Position = moveTailToHead(tail7Position, tail8Position)
                tail9Position = moveTailToHead(tail8Position, tail9Position)
                visitedTailPositions.add(tail9Position)
            }
        }

        return visitedTailPositions.distinct().size
    }


    data class Movement(
        val direction: String,
        val amount: Int
    )

    data class Position(
        val x: Int,
        val y: Int
    ) {
        fun touches(other: Position) : Boolean {
            return abs(other.x - this.x) <= 1 && abs(other.y - this.y) <= 1
        }
    }
}
