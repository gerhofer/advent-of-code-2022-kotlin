fun main(args: Array<String>) {
    println("Part 1: ${Day3.solvePart1()}")
    println("Part 2: ${Day3.solvePart2()}")
}

object Day3 {

    fun solvePart1(): Int {
        val input = Day3::class.java.getResource("day3.txt")?.readText() ?: error("Can't read input")
        val sumOfItemsInBothCompartments = input.split("\r\n")
            .sumOf { rucksack ->
                val parts = rucksack.chunked(rucksack.length / 2)
                val inBothCompartments = parts[0].first { parts[1].contains(it) }
                getPriority(inBothCompartments)
            }
        return sumOfItemsInBothCompartments
    }

    private fun getPriority(item: Char): Int {
        return if (item.isLowerCase()) {
            item.code - 96
        } else {
            item.code - 38
        }
    }

    fun solvePart2(): Int {
        val input = Day3::class.java.getResource("day3.txt")?.readText() ?: error("Can't read input")
        val sumOfBadgePriorities = input.split("\r\n")
            .chunked(3)
            .sumOf { elves ->
                val badge = elves[0].toSet().intersect(elves[1].toSet()).intersect(elves[2].toSet()).first()
                getPriority(badge)
            }
        return sumOfBadgePriorities
    }

}