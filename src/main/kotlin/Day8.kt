import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
    println("Part 1: ${Day8.solvePart1()}")
    println("Part 2: ${Day8.solvePart2()}")
}

object Day8 {

    fun solvePart1(): Long {
        val input = Day202020::class.java.getResource("day8.txt")?.readText() ?: error("Can't read input")
        val trees = input.split("\r\n")
            .map { treeLine -> treeLine.split("").filter { it.isNotBlank() }.map { tree -> tree.toInt() } }
        var visibileTreeCount = trees.size * 2 + trees[0].size * 2L - 4
        for (row in 1..trees.size - 2) {
            for (column in 1..trees[0].size - 2) {
                if (visibleFromAnyDirection(trees, row, column)) {
                    visibileTreeCount++
                }
            }
        }
        return visibileTreeCount
    }

    fun visibleFromAnyDirection(trees: List<List<Int>>, row: Int, column: Int): Boolean {
        val checkedTree = trees[row][column]
        val treeRow = trees[row]
        val treeColumn = trees.map { it[column] }
        return treeRow.take(column).all { it < checkedTree } ||
                treeRow.takeLast(treeRow.size - column - 1).all { it < checkedTree } ||
                treeColumn.take(row).all { it < checkedTree } ||
                treeColumn.takeLast(treeColumn.size - row - 1).all { it < checkedTree }
    }

    fun solvePart2(): Long {
        val input = Day202020::class.java.getResource("day8.txt")?.readText() ?: error("Can't read input")
        val trees = input.split("\r\n")
            .map { treeLine -> treeLine.split("").filter { it.isNotBlank() }.map { tree -> tree.toInt() } }
        val scenicScores = mutableListOf<Long>()
        for (row in 1..trees.size - 2) {
            for (column in 1..trees[0].size - 2) {
                scenicScores.add(getScenicScore(trees, row, column))
            }
        }
        return scenicScores.max()
    }

    fun getScenicScore(trees: List<List<Int>>, row: Int, column: Int): Long {
        val checkedTree = trees[row][column]
        val treeRow = trees[row]
        val treeColumn = trees.map { it[column] }
        val visibleToLeft = getScore(treeRow.take(column).reversed(), checkedTree)
        val visibleToRight = getScore(treeRow.takeLast(treeRow.size - column - 1), checkedTree)
        val visibleToTop = getScore(treeColumn.take(row).reversed(), checkedTree)
        val visibleToBottom = getScore(treeColumn.takeLast(treeColumn.size - row - 1), checkedTree)
        return visibleToLeft * visibleToRight * visibleToBottom * visibleToTop
    }

    fun getScore(trees: List<Int>, treeHouse: Int) : Long {
        var index = -1
        do {
            index++
        } while (index <= trees.size - 1 && trees[index] < treeHouse)
        return min(index.toLong() + 1L, trees.size.toLong())
    }

}
