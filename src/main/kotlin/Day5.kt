fun main(args: Array<String>) {
    println("Part 1: ${Day5.solvePart1()}")
    println("Part 2: ${Day5.solvePart2()}")
}

object Day5 {

    fun solvePart1(): String {
        val input = Day5::class.java.getResource("day5.txt")?.readText() ?: error("Can't read input")
        val startingPointAndMovements = input.split("\r\n\r\n")
        val craneStack = mutableMapOf<Int, MutableList<Char>>()
        val craneStackLines = startingPointAndMovements[0].split("\r\n")
        val stackCount = craneStackLines.last().trim().last().toString().toInt()

        craneStackLines.dropLast(1)
            .reversed()
            .map { line ->
                for (i in 0 until stackCount) {
                    val item = line[1 + i * 4]
                    if (item != ' ') {
                        val list = craneStack.getOrDefault(i, mutableListOf())
                        list.add(item)
                        craneStack[i] = list
                    }
                }
            }

        startingPointAndMovements[1].split("\r\n")
            .map { Movement(it) }
            .forEach { movement ->
                repeat(movement.amount) {
                    val fromStack = craneStack[movement.from - 1] ?: error("from stack ${movement.from} not found")
                    val toStack = craneStack[movement.to - 1] ?: error("from stack ${movement.from} not found")
                    val movedItem = fromStack.removeLast()
                    toStack.add(movedItem)
                }
            }

        return craneStack.values.map { it.last() }.joinToString("")
    }

    fun solvePart2(): String {
        val input = Day5::class.java.getResource("day5.txt")?.readText() ?: error("Can't read input")
        val startingPointAndMovements = input.split("\r\n\r\n")
        val craneStack = mutableMapOf<Int, MutableList<Char>>()
        val craneStackLines = startingPointAndMovements[0].split("\r\n")
        val stackCount = craneStackLines.last().trim().last().toString().toInt()

        craneStackLines.dropLast(1)
            .reversed()
            .map { line ->
                for (i in 0 until stackCount) {
                    val item = line[1 + i * 4]
                    if (item != ' ') {
                        val list = craneStack.getOrDefault(i, mutableListOf())
                        list.add(item)
                        craneStack[i] = list
                    }
                }
            }

        startingPointAndMovements[1].split("\r\n")
            .map { Movement(it) }
            .forEach { movement ->
                val fromStack = craneStack[movement.from - 1] ?: error("from stack ${movement.from} not found")
                val toStack = craneStack[movement.to - 1] ?: error("from stack ${movement.from} not found")
                val elementsToBeMoved = mutableListOf<Char>()
                repeat(movement.amount) {
                    elementsToBeMoved.add(fromStack.removeLast())
                }
                toStack.addAll(elementsToBeMoved.reversed())

            }

        return craneStack.values.map { it.last() }.joinToString("")
    }

}

data class Movement(
    val amount: Int,
    val from: Int,
    val to: Int,
) {
    constructor(string: String) : this(
        string.substringAfter("move ").substringBefore(" from").toInt(),
        string.substringAfter("from ").substringBefore(" to").toInt(),
        string.substringAfter("to ").toInt()
    )
}

