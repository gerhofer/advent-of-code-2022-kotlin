fun main(args: Array<String>) {
//    println("Part 1: ${Day21.solvePart1()}")
    println("Part 2: ${Day21.solvePart2()}")
}

object Day21 {
    fun solvePart1(): Long {
        val input = Day21::class.java.getResource("day21.txt")?.readText() ?: error("Can't read input")
        val yells = input.split("\r\n")
            .map { line ->
                val parts = line.split(": ")
                val number = parts[1].trim().toLongOrNull()
                if (number == null) {
                    MonkeyYell(parts[0].trim(), parts[1].trim(), null)
                } else {
                    MonkeyYell(parts[0].trim(), parts[1].trim(), number)
                }
            }

        val yellsLookup = yells.associateBy { it.key }

        while (yellsLookup.values.any { it.number == null }) {
            val (leaves, toBeReplaced) = yellsLookup.values.partition { it.number != null }
            for (numeric in leaves.filter { !it.replaced }) {
                toBeReplaced.forEach {
                    it.initial = it.initial.replace(numeric.key, numeric.number.toString())
                }
                numeric.replaced = true
            }
            toBeReplaced.forEach {
                it.evaluateIfPossible()
            }
        }

        return yellsLookup["root"]?.number!!
    }

    fun solvePart2(): Long {
        val input = Day21::class.java.getResource("day21.txt")?.readText() ?: error("Can't read input")
        val yells = input.split("\r\n")
            .map { line ->
                val parts = line.split(": ")
                val number = parts[1].trim().toLongOrNull()
                if (number == null) {
                    MonkeyYell(parts[0].trim(), parts[1].trim(), null)
                } else {
                    MonkeyYell(parts[0].trim(), parts[1].trim(), number)
                }
            }
        val yellsLookup = yells.associateBy { it.key }
        yellsLookup["humn"]!!.initial = "x"
        yellsLookup["humn"]!!.number = null
        yellsLookup["root"]!!.initial = yellsLookup["root"]!!.initial.replace("+", "=")

        while (yellsLookup.values.any { !it.replaced }) {
            val (toBeReplaced, replaceTargets) = yellsLookup.values.partition { it.containsNoKey(yellsLookup.keys) }
            for (numeric in toBeReplaced.filter { !it.replaced }) {
                replaceTargets.forEach {
                    if (numeric.initial == "x") {
                        it.initial = it.initial.replace(numeric.key, numeric.initial)
                    } else if (numeric.number == null) {
                        it.initial = it.initial.replace(numeric.key, "(" + numeric.initial + ")")
                    } else {
                        it.initial = it.initial.replace(numeric.key, numeric.number.toString())
                    }
                }
                numeric.replaced = true
            }
            replaceTargets.forEach {
                it.evaluateIfPossible()
            }
        }

        return reduce(yellsLookup["root"]?.initial!!)
    }

    fun reduce(math: String): Long {
        println(math)
        val parts = math.split(" = ")
        var mathContainingX = parts[0].trim()
        mathContainingX = mathContainingX.substring(1, mathContainingX.length - 1)
        var result = parts[1].trim().toLong()
        while (mathContainingX.contains("(")) {
            if (mathContainingX.startsWith("(")) {
                val operation = mathContainingX.substringAfterLast(")")
                mathContainingX = mathContainingX.substring(1, mathContainingX.length - (operation.length + 1))
                val operationParts = operation.trim().split(" ")
                val mathOp = operationParts[0].trim()
                val value = operationParts[1].trim().toLong()
                result = doInverse(result, mathOp, value)
                println("$mathContainingX with current result $result")
            } else if (mathContainingX.endsWith(")")) {
                val operation = mathContainingX.substringBefore("(")
                mathContainingX = mathContainingX.substring(operation.length + 1, mathContainingX.length - 1)
                val operationParts = operation.trim().split(" ")
                val mathOp = operationParts[1].trim()
                val value = operationParts[0].trim().toLong()
                result = doInverseTwo(result, mathOp, value)
                println("$mathContainingX with current result $result")
            } else {
                error("neither ( at beginning nor ) at end : ${mathContainingX}")
            }
        }
        return result
    }

    fun doInverseTwo(result: Long, operation: String, value: Long): Long {
        return if (operation == "/") {
            value * result
        } else if (operation == "+") {
            result - value
        } else if (operation == "-") {
            (result - value) * -1
        } else if (operation == "*") {
            result / value
        } else {
            error("Invalid math")
        }
    }

    fun doInverse(result: Long, operation: String, value: Long): Long {
        return if (operation == "/") {
            result * value
        } else if (operation == "+") {
            result - value
        } else if (operation == "-") {
            result + value
        } else if (operation == "*") {
            result / value
        } else {
            error("Invalid math")
        }
    }


    data class MonkeyYell(
        val key: String,
        var initial: String,
        var number: Long? = null,
        var replaced: Boolean = false,
    ) {

        fun containsNoKey(keys: Set<String>): Boolean {
            return number != null || keys.none { initial.contains(it) }
        }

        fun evaluateIfPossible() {
            val parts = initial.split(" ")
            if (parts.size == 3) {
                val first = parts[0].toLongOrNull()
                val operation = parts[1].trim()
                val second = parts[2].toLongOrNull()
                if (first != null && second != null) {
                    if (operation == "/") {
                        number = first / second
                    } else if (operation == "+") {
                        number = first + second
                    } else if (operation == "-") {
                        number = first - second
                    } else if (operation == "*") {
                        number = first * second
                    }
                }
            }
        }
    }
}