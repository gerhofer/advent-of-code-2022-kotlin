fun main(args: Array<String>) {
    println("Part 1: ${Day20.solvePart1()}")
    println("Part 2: ${Day20.solvePart2()}")
}

object Day20 {
    fun solvePart1(): Long {
        val input = Day20::class.java.getResource("day20.txt")?.readText() ?: error("Can't read input")
        val file = input.split("\r\n")
            .map { Element(it.toLong()) }
            .toMutableList()

        while (file.any { !it.encrypted }) {
            val initialIndex = file.indexOfFirst { !it.encrypted }
            val elementToMove = file.removeAt(initialIndex)
            elementToMove.encrypted = true
            val newIndex = (initialIndex + elementToMove.value) % file.size
            if (newIndex > 0) {
                file.add(newIndex.toInt(), elementToMove)
            } else {
                file.add(file.size + newIndex.toInt(), elementToMove)
            }
        }

        val indexOfZero = file.indexOfFirst { it.value == 0L }

        return file[(indexOfZero + 1000) % file.size].value + file[(indexOfZero + 2000) % file.size].value + file[(indexOfZero + 3000) % file.size].value
    }

    fun solvePart2(): Long {
        val input = Day20::class.java.getResource("day20.txt")?.readText() ?: error("Can't read input")
        val decryptionKey = 811589153L
        val originalFile = input.split("\r\n")
            .mapIndexed { index, it -> Element(it.toLong()*decryptionKey, index) }

        val file = originalFile.toMutableList()
        var encryptionRound = 0
        var indexInOriginalFile = 0

        while (encryptionRound < 10) {
            val initialIndex = file.indexOfFirst { it.originalIndex == indexInOriginalFile }
            val elementToMove = file.removeAt(initialIndex)
            elementToMove.encryptionCount++
            val newIndex = (initialIndex + elementToMove.value) % file.size
            if (newIndex > 0) {
                file.add(newIndex.toInt(), elementToMove)
            } else {
                file.add(file.size + newIndex.toInt(), elementToMove)
            }

            indexInOriginalFile++
            if (indexInOriginalFile == originalFile.size) {
                encryptionRound ++
                indexInOriginalFile = 0
                println("After Round $encryptionRound")
                println(file.map { it.value }.joinToString(", "))
            }
        }

        val indexOfZero = file.indexOfFirst { it.value == 0L }
        val first = file[(indexOfZero + 1000) % file.size].value
        val second = file[(indexOfZero + 2000) % file.size].value
        val third = file[(indexOfZero + 3000) % file.size].value
        println(first.toString() + " + " + second+ " + " + third)
        return first + second + third
    }

    data class Element(
        val value: Long,
        val originalIndex: Int = 0,
        var encrypted: Boolean = false,
        var encryptionCount: Int = 0
    )
}