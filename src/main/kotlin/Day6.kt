fun main(args: Array<String>) {
    println("Part 1: ${Day202020.solvePart1()}")
    println("Part 2: ${Day202020.solvePart2()}")
}

object Day6 {

    fun solvePart1(): Int {
        val input = Day202020::class.java.getResource("day6.txt")?.readText() ?: error("Can't read input")
        val firstIndex = input.windowed(4)
            .indexOfFirst {
                val letters = it.split("").filter { letter -> letter.isNotBlank() }
                letters.distinct().size == 4
            }
        return firstIndex + 4
    }

    fun solvePart2(): Int {
        val input = Day202020::class.java.getResource("day6.txt")?.readText() ?: error("Can't read input")
        val firstIndex = input.windowed(14)
            .indexOfFirst {
                val letters = it.split("").filter { letter -> letter.isNotBlank() }
                letters.distinct().size == 14
            }
        return firstIndex + 14
    }

}
