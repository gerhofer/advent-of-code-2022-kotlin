fun main(args: Array<String>) {
    println("Part 1: ${Day1.solvePart1()}")
    println("Part 2: ${Day1.solvePart2()}")
}

object Day1 {
    fun solvePart1(): Int {
        val input = Day1::class.java.getResource("day1.txt")?.readText() ?: error("Can't read input")
        val sortedCalories = input.split("\r\n\r\n")
            .map { it.split("\r\n").sumOf { calorie -> calorie.toInt() } }
            .sorted()
        return sortedCalories.last()
    }

    fun solvePart2(): Int {
        val input = Day1::class.java.getResource("day1.txt")?.readText() ?: error("Can't read input")
        val sortedCalories = input.split("\r\n\r\n")
            .map { it.split("\r\n").sumOf { calorie -> calorie.toInt() } }
            .sorted()
        return sortedCalories.takeLast(3).sum()
    }
}