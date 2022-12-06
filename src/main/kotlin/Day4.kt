fun main(args: Array<String>) {
    println("Part 1: ${Day4.solvePart1()}")
    println("Part 2: ${Day4.solvePart2()}")
}

object Day4 {

    fun solvePart1(): Int {
        val input = Day4::class.java.getResource("day4.txt")?.readText() ?: error("Can't read input")
        val numberOfElvesCleaningOnlyCleanThings = input.split("\r\n")
            .count { line ->
                val elves = ElvePair(line)
                elves.isAnyElveRedundant()
            }
        return numberOfElvesCleaningOnlyCleanThings
    }

    fun solvePart2(): Int {
        val input = Day4::class.java.getResource("day4.txt")?.readText() ?: error("Can't read input")
        val noOverlap = input.split("\r\n")
            .count { line ->
                val elves = ElvePair(line)
                elves.hasAnyOverlap()
            }
        return noOverlap
    }

}

class ElvePair(
    val firstElve: LongRange,
    val secondElve: LongRange
) {
    constructor(asString: String) : this(
        toRange(asString.substringBefore(',')),
        toRange(asString.substringAfter(','))
    )

    fun isAnyElveRedundant(): Boolean =
        (firstElve.contains(secondElve.first) && firstElve.contains(secondElve.last)) ||
                (secondElve.contains(firstElve.first) && secondElve.contains(firstElve.last))

    fun hasAnyOverlap(): Boolean =
        firstElve.contains(secondElve.first) || firstElve.contains(secondElve.last) ||
                secondElve.contains(firstElve.first) || secondElve.contains(firstElve.last)
}

fun toRange(rangeAsString: String): LongRange {
    val lower = rangeAsString.substringBefore("-").toLong()
    val upper = rangeAsString.substringAfter("-").toLong()
    return LongRange(lower, upper)
}