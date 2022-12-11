fun main(args: Array<String>) {
    println("Part 1: ${Day10.solvePart1()}")
    println("Part 2: \n${Day10.solvePart2()}")
}

object Day10 {
    fun solvePart1(): Int {
        val input = Day202020::class.java.getResource("day10.txt")?.readText() ?: error("Can't read input")
        val commands = input.split("\r\n")
            .map { it.split(" ") }
            .map {
                if (it.size > 1) {
                    Command(it[0], it[1].toInt())
                } else {
                    Command(it[0], null)
                }
            }
        val cycleMeasuringTimes = listOf(20, 60, 100, 140, 180, 220)
        val cycleMeasurements = mutableListOf<Int>()
        var cycle = 1
        var x = 1
        for (command in commands) {
            cycle++
            if (cycleMeasuringTimes.contains(cycle)) {
                cycleMeasurements.add(x)
            }
            if (command.command == "addx") {
                cycle++
                x += command.amount ?: error("found addx command without value")
                if (cycleMeasuringTimes.contains(cycle)) {
                    cycleMeasurements.add(x)
                }
            }
        }

        return cycleMeasurements.zip(cycleMeasuringTimes)
            .sumOf { pair -> pair.first * pair.second }
    }

    fun solvePart2(): String {
        val input = Day202020::class.java.getResource("day10.txt")?.readText() ?: error("Can't read input")
        val commands = input.split("\r\n")
            .map { it.split(" ") }
            .map {
                if (it.size > 1) {
                    Command(it[0], it[1].toInt())
                } else {
                    Command(it[0], null)
                }
            }
        var cycle = 1
        var x = 1
        var result = ""
        for (command in commands) {
            result += if (((cycle-1) % 40) in (x - 1..x + 1)) {
                "#"
            } else {
                "."
            }
            cycle++
            if (command.command == "addx") {
                result += if (((cycle-1) % 40) in (x - 1..x + 1)) {
                    "#"
                } else {
                    "."
                }
                cycle++
                x += command.amount ?: error("found addx command without value")
            }
        }

        println(result)

        return result.chunked(40).joinToString("\n")
    }

    data class Command(
        val command: String,
        val amount: Int?,
    )
}
