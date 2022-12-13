import kotlin.math.sign

fun main(args: Array<String>) {
    println("Part 1: ${Day13.solvePart1()}")
    println("Part 2: ${Day13.solvePart2()}")
}

object Day13 {
    fun solvePart1(): Int {
        val input = Day13::class.java.getResource("day13.txt")?.readText() ?: error("Can't read input")
        val pairs = input.split("\r\n\r\n")
            .map { it.split("\r\n") }

        return pairs.mapIndexed { index, pair ->
            if (isCorrectOrder(pair[0], pair[1])) {
                index + 1
            } else {
                0
            }
        }.sum()
    }

    private fun parse(input: String): List<Element> {
        val listStack = mutableListOf<MutableList<Element>>()
        var letterIdx = 0
        while (letterIdx < input.length - 1) {
            when (val letter = input[letterIdx]) {
                '[' -> {
                    listStack.add(mutableListOf())
                    letterIdx++
                }

                ']' -> {
                    val closedList = listStack.removeLast()
                    listStack.last().add(Element(null, closedList.toList()))
                    letterIdx++
                }
                ',' -> {
                    letterIdx++
                }
                else -> {
                    if (letter !in listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')) {
                        println("found $letter at $letterIdx")
                    }
                    val digit = substringUntilCommaOrEndParenthesis(input.substring(letterIdx))
                    letterIdx += digit.length
                    listStack.last().add(Element(digit.toInt(), null))
                }
            }
        }
        check(listStack.size == 1)
        return listStack.first()
    }

    private fun substringUntilCommaOrEndParenthesis(input: String): String {
        var digit = ""
        var idx = 0
        while (input[idx] != ',' && input[idx] != ']') {
            digit += input[idx]
            idx++
        }
        return digit
    }

    private fun isCorrectOrder(first: String, second: String): Boolean {
        val firstList = Element(null, parse(first))
        val secondList = Element(null, parse(second))
        return isCorrectOrder(firstList, secondList) ?: error("Could not determine for $firstList and $secondList")
    }

    private fun isCorrectOrder(first: Element, second: Element): Boolean? {
        if (first.numericValue != null && second.numericValue != null) {
            return if (first.numericValue < second.numericValue) {
                true
            } else if (first.numericValue > second.numericValue) {
                false
            } else {
                null // continue search
            }
        } else {
            val firstList = first.listValue ?: listOf(Element(first.numericValue, null))
            var index = 0
            val secondList = second.listValue ?: listOf(Element(second.numericValue, null))
            while (true) {
                if (index >= firstList.size && index >= secondList.size) {
                    return null
                } else if (index >= firstList.size) {
                    return true
                } else if (index >= secondList.size) {
                    return false
                }
                val areCorrectOrder = isCorrectOrder(firstList[index], secondList[index])
                if (areCorrectOrder != null) {
                    return areCorrectOrder
                }
                index++
            }
        }
    }

    fun solvePart2(): Int {
        val input = Day13::class.java.getResource("day13.txt")?.readText() ?: error("Can't read input")
        val packets = input.split("\r\n\r\n")
            .flatMap { it.split("\r\n") }
            .map { parse(it) }

        val packetsBefore2 = packets.count {
            isCorrectOrder(Element(null, it), Element(null, listOf(Element(2, null)))) ?: error("Could not determine")
        }

        val packetsBefore6 = packets.count {
            isCorrectOrder(Element(null, it), Element(null, listOf(Element(6, null)))) ?: error("Could not determine")
        }

        return (packetsBefore2 + 1) * (packetsBefore6 + 2)
    }

    data class Element(
        val numericValue: Int?,
        val listValue: List<Element>?,
    )

}
