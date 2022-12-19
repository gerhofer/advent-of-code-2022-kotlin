fun main(args: Array<String>) {
    println("Part 1: ${Day19.solvePart1()}")
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

        val maxGeodes = blueprints.map { getMaxGeodesForBluePrint(it) }
        return maxGeodes.max()
    }

    private fun getMaxGeodesForBluePrint(bluePrint: BluePrint) : Int {
        var minutesPassed = 0
        var credits = setOf(Credit(1, 0, 0, 0,0, 0, 0, 0))
        while (minutesPassed < 24) {
            val newCredits = credits.map {
                val possibilities = mutableSetOf<Credit>()
                var newOreRobots = 0
                var newClayRobots = 0
                var newObsidianRobots = 0
                var newGeodeRobots = 0
                if (it.canAfford(bluePrint.obsidianRobotCosts)) {
                    newObsidianRobots++
                }
                if (it.canAfford(bluePrint.geodeRobotCosts)) {
                    newGeodeRobots++
                }
                if (it.canAfford(bluePrint.clayRobotCosts)) {
                    newClayRobots++
                }
                if (it.canAfford(bluePrint.oreRobotCosts)) {
                    newOreRobots++
                }
                it.farm()
                possibilities.add(it)
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
            credits = newCredits.flatten().toSet()
            minutesPassed++
        }

        return credits.maxOf { it.geodes }
    }

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

        fun canAfford(cost: Cost) : Boolean {
            return this.ore >= cost.ore && this.clay >= cost.clay && this.obsidian >= cost.obsidian
        }

        fun deduct(cost: Cost) {
            this.ore -= cost.ore
            this.clay -= cost.clay
            this.obsidian -= cost.obsidian
        }

    }

    data class Cost(
        val ore: Int,
        val clay: Int,
        val obsidian: Int
    )

    fun solvePart2(): Int {
        val input = Day19::class.java.getResource("day19.txt")?.readText() ?: error("Can't read input")

        return -1
    }
}