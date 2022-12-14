import kotlin.math.floor

fun main(args: Array<String>) {
    println("Part 1: ${Day11.solvePart1()}")
    println("Part 2: ${Day11.solvePart2()}")
}

object Day11 {
    fun solvePart1(): Int {
        val input = Day11::class.java.getResource("day11.txt")?.readText() ?: error("Can't read input")
        val monkeys = input.split("\r\n\r\n")
            .map {
                parseMonkey(it)
            }.associateBy { it.index }
        val monkeyInspectionCount = monkeys.keys.associateWith { 0 }
            .toMutableMap()
        repeat(100) {
            for (monkeyEntry in monkeys) {
                val monkey = monkeyEntry.value
                monkeyInspectionCount[monkey.index] = monkeyInspectionCount[monkey.index]!! + monkey.items.size
                for (worryLevel in monkey.items) {
                    var newWorryLevel =
                        floor(eval(monkey.worryOperation.replace("old", worryLevel.toString())) / 3.0).toLong()
                    if (newWorryLevel % monkey.divisible == 0L) {
                        monkeys[monkey.trueGoal]!!.items.add(newWorryLevel)
                    } else {
                        monkeys[monkey.falseGoal]!!.items.add(newWorryLevel)
                    }
                }
                monkey.items.clear()
            }
        }

        println(monkeyInspectionCount)
        return monkeyInspectionCount.values.sortedDescending().take(2).reduce { a, b -> a * b }
    }

    private fun eval(math: String): Long {
        return if (math.contains("+")) {
            math.substringBefore("+").trim().toLong() + math.substringAfter("+").trim().toLong()
        } else {
            math.substringBefore("*").trim().toLong() * math.substringAfter("*").trim().toLong()
        }
    }

    private fun parseMonkey(it: String): Monkey {
        val monkeyLines = it.split("\r\n")
        return Monkey(
            monkeyLines[0].substringAfter("Monkey ").substringBefore(":").toInt(),
            monkeyLines[2].substringAfterLast("new = "),
            monkeyLines[3].substringAfterLast(" ").toLong(),
            monkeyLines[4].substringAfterLast(" ").toInt(),
            monkeyLines[5].substringAfterLast(" ").toInt(),
            monkeyLines[1].substringAfter("Starting items:").split(",").filter { it.isNotBlank() }
                .map { it.trim().toLong() }.toMutableList(),

            )
    }

    private fun updateModTable(modTable: Map<Long, Long>, operation: String): Map<Long, Long> {
        return if (operation.contains("+")) {
            val add = operation.substringAfter("+").trim().toLong()
            modTable.mapValues { entry ->
                (entry.value + add) % entry.key
            }
        } else {
            if (operation.trim().endsWith("old")) {
                modTable.mapValues { entry ->
                    (entry.value * entry.value) % entry.key
                }
            } else {
                val add = operation.substringAfter("*").trim().toLong()
                modTable.mapValues { entry ->
                    (entry.value * add) % entry.key
                }
            }
        }
    }

    fun solvePart2(): Long {
        val input = Day11::class.java.getResource("day11.txt")?.readText() ?: error("Can't read input")
        val monkeys = input.split("\r\n\r\n")
            .map { parseMonkey(it) }
            .associateBy { it.index }
        val monkeyInspectionCount = monkeys.keys.associateWith { 0L }
            .toMutableMap()
        val allDivisionChecks = monkeys.values
            .map { it.divisible }

        for (monkey in monkeys) {
            for (item in monkey.value.items) {
                val moduloResults = allDivisionChecks.associateWith { item % it }
                monkey.value.itemsImproved.add(Item(item, moduloResults.toMutableMap()))
            }
        }

        repeat(10000) {
            for (monkeyEntry in monkeys) {
                val monkey = monkeyEntry.value
                monkeyInspectionCount[monkey.index] = monkeyInspectionCount[monkey.index]!! + monkey.itemsImproved.size.toLong()
                for (item in monkey.itemsImproved) {
                    val newModuloMap = updateModTable(item.moduloMap, monkey.worryOperation)
                    if (newModuloMap[monkey.divisible]!! == 0L) {
                        monkeys[monkey.trueGoal]!!.itemsImproved.add(Item(0L, newModuloMap))
                    } else {
                        monkeys[monkey.falseGoal]!!.itemsImproved.add(Item(0L, newModuloMap))
                    }
                }
                monkey.itemsImproved.clear()
            }
        }

        println(monkeyInspectionCount)
        return monkeyInspectionCount.values.sortedDescending().take(2).reduce { a, b -> a * b }
    }

    data class Monkey(
        val index: Int,
        val worryOperation: String,
        val divisible: Long,
        val trueGoal: Int,
        val falseGoal: Int,
        val items: MutableList<Long>,
        val itemsImproved: MutableList<Item> = mutableListOf()
    )

    data class Item(
        val originalValue: Long = 0L,
        val moduloMap: Map<Long, Long>
    )
}
