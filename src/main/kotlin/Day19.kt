import kotlin.math.max

fun main(args: Array<String>) {
    // println("Part 1: ${Day19.solvePart1()}")
    println("Part 2: ${Day19.solvePart2()}")
}

object Day19 {

    val bluePrintRegex = ("Blueprint ([0-9]+): Each ore robot costs ([0-9]+) ore. " +
            "Each clay robot costs ([0-9]+) ore. " +
            "Each obsidian robot costs ([0-9]+) ore and ([0-9]+) clay. " +
            "Each geode robot costs ([0-9]+) ore and ([0-9]+) obsidian.").toRegex()


    fun solvePart1(): Int {
        val input = Day19::class.java.getResource("day19.txt")?.readText() ?: error("Can't read input")
        val blueprints = input.split("\r\n")
            .map {
                val (id,
                    oreRobotCostsInOre,
                    clayRobotCostsInOre,
                    obsidianRobotCostsInOre,
                    obsidianRobotCostsInClas,
                    geodeRobotCostsInOre,
                    geodeRobotCostsInObsidian) = bluePrintRegex.matchEntire(it)?.destructured
                    ?: error("Does not match input")
                BluePrint(
                    id.toInt(),
                    Cost(oreRobotCostsInOre.toInt(), 0, 0),
                    Cost(clayRobotCostsInOre.toInt(), 0, 0),
                    Cost(obsidianRobotCostsInOre.toInt(), obsidianRobotCostsInClas.toInt(), 0),
                    Cost(geodeRobotCostsInOre.toInt(), 0, geodeRobotCostsInObsidian.toInt())
                )
            }

        // 31, 8
        val maxGeodes = blueprints.associate { it.id to getMaxGeodesForBluePrint(it) }
        return maxGeodes.map { it.key * it.value }.sum()
    }

    private fun getMaxGeodesForBluePrint(bluePrint: BluePrint, time: Int = 24): Int {
        val maxOreCost = listOf(
            bluePrint.oreRobotCosts.ore,
            bluePrint.clayRobotCosts.ore,
            bluePrint.obsidianRobotCosts.ore,
            bluePrint.geodeRobotCosts.ore
        ).max()
        val maxClayCost = bluePrint.obsidianRobotCosts.clay
        val maxObsidianCost = bluePrint.geodeRobotCosts.obsidian
        var minutesPassed = 0
        var credits = setOf(Credit(1, 0, 0, 0, 0, 0, 0, 0))
        while (minutesPassed < time) {
            val newCredits = credits.map {
                val possibilities = mutableSetOf<Credit>()
                var newOreRobots = 0
                var newClayRobots = 0
                var newObsidianRobots = 0
                var newGeodeRobots = 0

                if (it.canAfford(bluePrint.geodeRobotCosts)) {
                    newGeodeRobots++
                } else {
                    if (it.canAfford(bluePrint.obsidianRobotCosts) && it.obsidianRobots < maxObsidianCost) {
                        newObsidianRobots++
                    }
                    if (it.canAfford(bluePrint.clayRobotCosts) && it.clayRobots < maxClayCost) {
                        newClayRobots++
                    }
                    if (it.canAfford(bluePrint.oreRobotCosts) && it.oreRobots < maxOreCost) {
                        newOreRobots++
                    }
                }
                it.farm()
                if (newGeodeRobots <= 0) {
                    possibilities.add(it)
                }
                if (newObsidianRobots > 0) {
                    val obsidianCreation = it.copy(obsidianRobots = it.obsidianRobots + 1)
                    obsidianCreation.deduct(bluePrint.obsidianRobotCosts)
                    possibilities.add(obsidianCreation)
                }
                if (newClayRobots > 0) {
                    val clayCreation = it.copy(clayRobots = it.clayRobots + 1)
                    clayCreation.deduct(bluePrint.clayRobotCosts)
                    possibilities.add(clayCreation)
                }
                if (newOreRobots > 0) {
                    val oreCreation = it.copy(oreRobots = it.oreRobots + 1)
                    oreCreation.deduct(bluePrint.oreRobotCosts)
                    possibilities.add(oreCreation)
                }
                if (newGeodeRobots > 0) {
                    val geodeCreation = it.copy(geodeRobots = it.geodeRobots + 1)
                    geodeCreation.deduct(bluePrint.geodeRobotCosts)
                    possibilities.add(geodeCreation)
                }
                possibilities
            }
            credits = keepBestOnly(newCredits.flatten().toSet())
            minutesPassed++
        }

        // 30, 8, 17
        println(credits.maxOf { it.geodes })
        return credits.maxOf { it.geodes }
    }

    private fun keepBestOnly(creditOptions: Set<Credit>): Set<Credit> {
        val grouped = creditOptions.groupBy { Important(it.oreRobots, it.clayRobots, it.obsidianRobots, it.geodeRobots, it.obsidian, it.geodes, it.clay) }
        return grouped.map { it.value.maxBy { it.ore } }.toSet()
    }

    data class Important(
        var oreRobots: Int,
        var clayRobots: Int,
        var obsidianRobots: Int,
        var geodeRobots: Int,
        var obsidian: Int,
        var geodes: Int,
        var clay: Int,
    )

    data class BluePrint(
        val id: Int,
        val oreRobotCosts: Cost,
        val clayRobotCosts: Cost,
        val obsidianRobotCosts: Cost,
        val geodeRobotCosts: Cost
    )

    data class Credit(
        var oreRobots: Int,
        var clayRobots: Int,
        var obsidianRobots: Int,
        var geodeRobots: Int,
        var ore: Int,
        var clay: Int,
        var obsidian: Int,
        var geodes: Int,
    ) {
        fun farm() {
            this.ore += oreRobots
            this.clay += clayRobots
            this.obsidian += obsidianRobots
            this.geodes += geodeRobots
        }

        fun canAfford(cost: Cost): Boolean {
            return this.ore >= cost.ore && this.clay >= cost.clay && this.obsidian >= cost.obsidian
        }

        fun deduct(cost: Cost) {
            this.ore -= cost.ore
            this.clay -= cost.clay
            this.obsidian -= cost.obsidian
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Credit

            if (oreRobots != other.oreRobots) return false
            if (clayRobots != other.clayRobots) return false
            if (obsidianRobots != other.obsidianRobots) return false
            if (geodeRobots != other.geodeRobots) return false
            if (ore != other.ore) return false
            if (clay != other.clay) return false
            if (obsidian != other.obsidian) return false
            if (geodes != other.geodes) return false

            return true
        }

        override fun hashCode(): Int {
            var result = oreRobots
            result = 31 * result + clayRobots
            result = 31 * result + obsidianRobots
            result = 31 * result + geodeRobots
            result = 31 * result + ore
            result = 31 * result + clay
            result = 31 * result + obsidian
            result = 31 * result + geodes
            return result
        }


    }

    data class Cost(
        val ore: Int,
        val clay: Int,
        val obsidian: Int
    )

    fun solvePart2(): Long {
        val input = Day19::class.java.getResource("day19.txt")?.readText() ?: error("Can't read input")
        val blueprints = input.split("\r\n")
            .map {
                val (id,
                    oreRobotCostsInOre,
                    clayRobotCostsInOre,
                    obsidianRobotCostsInOre,
                    obsidianRobotCostsInClas,
                    geodeRobotCostsInOre,
                    geodeRobotCostsInObsidian) = bluePrintRegex.matchEntire(it)?.destructured
                    ?: error("Does not match input")
                BluePrint(
                    id.toInt(),
                    Cost(oreRobotCostsInOre.toInt(), 0, 0),
                    Cost(clayRobotCostsInOre.toInt(), 0, 0),
                    Cost(obsidianRobotCostsInOre.toInt(), obsidianRobotCostsInClas.toInt(), 0),
                    Cost(geodeRobotCostsInOre.toInt(), 0, geodeRobotCostsInObsidian.toInt())
                )
            }

        val maxGeodes = blueprints
            .take(3)
            .map { getMaxGeodesForBluePrint(it, 32).toLong() }
        println(maxGeodes)
        return maxGeodes.reduce { acc, i -> acc * i }
    }
}