fun main(args: Array<String>) {
    println("Part 1: ${Day2.solvePart1()}")
    println("Part 2: ${Day2.solvePart2()}")
}

object Day2 {
    fun solvePart1(): Int {
        val input = Day2::class.java.getResource("day2.txt")?.readText() ?: error("Can't read input")
        val totalScore = input.split("\r\n")
            .sumOf {
                val opponent = it.substringBefore(" ")
                val player = it.substringAfter(" ")
                val scoreFromChoice = getScoreFromChoice(player)
                val scoreFromOutcome = getScoreFromOutcome(opponent, player)
                scoreFromChoice + scoreFromOutcome
            }
        return totalScore
    }

    private fun getScoreFromOutcome(opponent: String, player: String): Int {
        val scoreFromOutcome = if ((opponent == "A" && player == "Y") ||
            (opponent == "B" && player == "Z") ||
            (opponent == "C" && player == "X")
        ) {
            6
        } else if ((opponent == "A" && player == "X") ||
            (opponent == "B" && player == "Y") ||
            (opponent == "C" && player == "Z")
        ) {
            3
        } else {
            0
        }
        return scoreFromOutcome
    }

    private fun getScoreFromChoice(player: String): Int {
        val scoreFromChoice = when (player) {
            "X" -> 1 // Rock
            "Y" -> 2 // Paper
            "Z" -> 3 // Scissors
            else -> error("Invalid Rock Paper Scissor value")
        }
        return scoreFromChoice
    }

    fun solvePart2(): Int {
        val input = Day2::class.java.getResource("day2.txt")?.readText() ?: error("Can't read input")
        val totalScore = input.split("\r\n")
            .sumOf {
                val opponent = it.substringBefore(" ")
                val outcome = it.substringAfter(" ")
                val scoreFromChoice = getScoreFromOutcome(outcome)
                val scoreFromOutcome = getScoreFromChoice(opponent, outcome)
                scoreFromChoice + scoreFromOutcome
            }
        return totalScore
    }

    private fun getScoreFromChoice(opponent: String, outcome: String): Int {
        val scoreFromOutcome = if ((opponent == "A" && outcome == "Y") ||
            (opponent == "B" && outcome == "X") ||
            (opponent == "C" && outcome == "Z")
        ) {
            1 // Rock
        } else if ((opponent == "A" && outcome == "Z") ||
            (opponent == "B" && outcome == "Y") ||
            (opponent == "C" && outcome == "X")
        ) {
            2 // Paper
        } else {
            3
        }
        return scoreFromOutcome
    }

    private fun getScoreFromOutcome(outcome: String): Int {
        val scoreFromChoice = when (outcome) {
            "X" -> 0 // Lose
            "Y" -> 3 // Draw
            "Z" -> 6 // Win
            else -> error("Invalid Rock Paper Scissor value")
        }
        return scoreFromChoice
    }
}