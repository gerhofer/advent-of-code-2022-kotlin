import kotlin.math.max

fun main(args: Array<String>) {
    println("Part 1: ${Day16.solvePart1()}")
    println("Part 2: ${Day16.solvePart2()}")
}

object Day16 {
    fun solvePart1(): Long {
        val input = Day16::class.java.getResource("day16.txt")?.readText() ?: error("Can't read input")
        val valves = input.split("\r\n")
            .map { readValve(it) }
            .associateBy { it.id }

        val valvesWorthOpening = valves.values.filter { it.pressure > 0 }.map { it.id }.toSet()
        val pathsPerCave = valves.values.associate { it.id to getPathMap(valves, it) }
        val startingValve = valves["AA"] ?: error("Valve AA not found")
        val possibleMoves = performPossibleMoves(
            valves,
            valvesWorthOpening,
            pathsPerCave,
            setOf(Movement(0, 0, startingValve, listOf()))
        )

        return possibleMoves.maxOf { it.currentPressure }
    }

    // map from valve to pathSize
    private fun getPathMap(valves: Map<String, Valve>, startingValve: Valve): Map<String, Int> {
        val pathMap = mutableMapOf<String, Int>()
        val valvesToVisit = valves.values.filter { it != startingValve }
            .toMutableSet()
        fillPathMap(valves, pathMap, startingValve, valvesToVisit, 0)
        pathMap.remove(startingValve.id)
        return pathMap
    }

    private fun fillPathMap(
        valves: Map<String, Valve>,
        pathMap: MutableMap<String, Int>,
        current: Valve,
        valvesToVisit: MutableSet<Valve>,
        pathSize: Int
    ) {
        if (pathSize < valves.size - 1) {
            for (option in current.reachableValves) {
                val optionValve = valves[option] ?: error("Valve $option not found")
                valvesToVisit.remove(optionValve)
                val existingPathSize = pathMap.getOrDefault(option, valves.size + 1)
                if ((pathSize + 1) < existingPathSize) {
                    pathMap[option] = pathSize + 1
                    fillPathMap(
                        valves,
                        pathMap,
                        optionValve,
                        valvesToVisit,
                        pathSize + 1
                    )
                }
            }
        }
    }

    private fun performPossibleMoves(
        valves: Map<String, Valve>,
        valvesWorthOpening: Set<String>,
        pathsFromValves: Map<String, Map<String, Int>>,
        movements: Set<Movement>
    ): Set<Movement> {
        val newMovements = movements.map {
            if (it.currentMinute >= 30) {
                setOf(it)
            } else {
                val options = mutableListOf<Movement>()
                val pressureIncreasePerStep = it.openValves.sumOf { opened -> opened.pressure }
                val reachableValves = pathsFromValves[it.currentValve.id] ?: emptyMap()
                val possibleSteps =
                    reachableValves.keys.intersect(valvesWorthOpening) - it.openValves.map { v -> v.id }.toSet()
                if (possibleSteps.isNotEmpty()) {
                    for (option in possibleSteps) {
                        val pathSize = reachableValves[option] ?: error("Can't reach option $option")
                        if (it.currentMinute + pathSize + 1 > 30) {
                            //println("Current is $it and we stay here till the end")
                            options.add(
                                Movement(
                                    30,
                                    it.currentPressure + (30 - it.currentMinute) * pressureIncreasePerStep,
                                    it.currentValve,
                                    it.openValves
                                )
                            )
                        } else {
                            val newValve = valves[option] ?: error("Valve $option not found")
                            val newPressure = it.currentPressure + (pathSize + 1) * pressureIncreasePerStep
                            val openValves = it.openValves.toMutableList()
                            openValves.add(newValve)
                            //println("Opening $newValve increased pressure ${(pathSize + 1)} times by stepIncrease = $pressureIncreasePerStep")
                            options.addAll(
                                performPossibleMoves(
                                    valves,
                                    valvesWorthOpening,
                                    pathsFromValves,
                                    setOf(
                                        Movement(
                                            it.currentMinute + pathSize + 1,
                                            newPressure,
                                            newValve,
                                            openValves
                                        )
                                    )
                                )
                            )
                        }
                    }
                } else {
                    val missingSteps = (30 - it.currentMinute)
                    //println("Everything that needs opening is open staying for $missingSteps by stepIncrease = $pressureIncreasePerStep")
                    options.add(
                        Movement(
                            30,
                            it.currentPressure + missingSteps * pressureIncreasePerStep,
                            it.currentValve,
                            it.openValves
                        )
                    )
                }
                options.toList()
            }
        }
        return newMovements.flatten().toSet()
    }

    private fun readValve(line: String): Valve {
        // Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        val id = line.substringAfter("Valve ").substringBefore(" has")
        val pressure = line.substringAfter("rate=").substringBefore(";").toInt()
        val connectedValves = if (line.contains("to valves ")) {
            line.substringAfter("to valves ").split(", ")
        } else {
            listOf(line.substringAfter("to valve ").trim())
        }
        return Valve(id, pressure, connectedValves)
    }

    var MAX_TIL_NOW = 0L
    val existingStates = mutableSetOf<State>()

    fun solvePart2(): Long {
        val input = Day16::class.java.getResource("day16.txt")?.readText() ?: error("Can't read input")
        val valves = input.split("\r\n")
            .map { readValve(it) }
            .associateBy { it.id }

        val valvesWorthOpening = valves.values.filter { it.pressure > 0 }.map { it.id }.toSet()
        val pathsPerCave = valves.values.associate { it.id to getPathMap(valves, it) }
        //val startingValve = valves["AA"] ?: error("Valve AA not found")
        //performPossibleMovesWithElephant(
        //    valves,
        //    valvesWorthOpening,
        //    pathsPerCave,
        //    setOf(PairMovement(0, startingValve, 0, startingValve, listOf())),
        //)

        val startingPossibilites = generateStartingPossibilities(valvesWorthOpening, pathsPerCave)
        val threeOpen = keepBest(generatePossiblities(startingPossibilites, valvesWorthOpening, pathsPerCave), valves)
        val fourOpen = keepBest(generatePossiblities(threeOpen, valvesWorthOpening, pathsPerCave), valves)
        val fiveOpen = keepBest(generatePossiblities(fourOpen, valvesWorthOpening, pathsPerCave), valves)
        val sixOpen = keepBest(generatePossiblities(fiveOpen, valvesWorthOpening, pathsPerCave), valves)
        val sevenOpen = keepBest(generatePossiblities(sixOpen, valvesWorthOpening, pathsPerCave), valves)
        val eightOpen = keepBest(generatePossiblities(sevenOpen, valvesWorthOpening, pathsPerCave), valves)
        val nineOpen = keepBest(generatePossiblities(eightOpen, valvesWorthOpening, pathsPerCave), valves)
        val tenOpen = keepBest(generatePossiblities(nineOpen, valvesWorthOpening, pathsPerCave), valves)
        val elevenOpen = keepBest(generatePossiblities(tenOpen, valvesWorthOpening, pathsPerCave), valves)
        val twelveOpen = keepBest(generatePossiblities(elevenOpen, valvesWorthOpening, pathsPerCave), valves)
        val thirteenOpen = keepBest(generatePossiblities(twelveOpen, valvesWorthOpening, pathsPerCave), valves)
        val fourteenOpen = keepBest(generatePossiblities(thirteenOpen, valvesWorthOpening, pathsPerCave), valves)
        val fifteenOpen = keepBest(generatePossiblities(fourteenOpen, valvesWorthOpening, pathsPerCave), valves)

        return fifteenOpen.maxOfOrNull { calculateTotalPressure(it, valves) }!!
    }

    private fun keepBest(states: List<List<ValveOpen>>, allvalves: Map<String, Valve>): List<List<ValveOpen>> {
        return states
            .sortedBy { calculateTotalPressure(it, allvalves) }
            .takeLast(3000000)
    }

    private fun calculateTotalPressure(
        valveOpenings: List<ValveOpen>,
        valves: Map<String, Valve>,
        until: Int = 26
    ): Long {
        return valveOpenings
            .filter { it.minute <= until }
            .sumOf {
                valves[it.valve]!!.pressure.toLong() * (until - it.minute)
            }
    }

    private fun calculateTotalPressure(valveOpenings: List<ValveOpening>, until: Int = 26): Long {
        return valveOpenings.sumOf {
            it.valve.pressure.toLong() * (until - it.minute)
        }
    }

    private fun performPossibleMovesWithElephant(
        valves: Map<String, Valve>,
        valvesWorthOpening: Set<String>,
        pathsFromValves: Map<String, Map<String, Int>>,
        movements: Set<PairMovement>,
    ): Set<PairMovement> {
        val newMovements = movements.map {
            if (it.currentMinute >= 26 && it.elephantMinute >= 26 || it.openValves.size == valvesWorthOpening.size) {
                val pressure = calculateTotalPressure(it.openValves)
                if (pressure > MAX_TIL_NOW) {
                    println("Found new maxima $pressure")
                    MAX_TIL_NOW = pressure
                }
                setOf(it)
            } else {
                val pressureToNow = calculateTotalPressure(it.openValves, max(it.currentMinute, it.elephantMinute))
                val increaseIfAllOpen = valvesWorthOpening.sumOf { valves[it]?.pressure ?: 0 }
                if (pressureToNow + increaseIfAllOpen * (26 - it.currentMinute) < MAX_TIL_NOW) {
                    // println("Skipping because can't reach existing maxima")
                    emptySet()
                } else {
                    val options = mutableListOf<PairMovement>()
                    val reachableValvesElephant = pathsFromValves[it.elephantValve.id] ?: emptyMap()
                    val possibleStepsElephant =
                        reachableValvesElephant.keys.intersect(valvesWorthOpening) - it.openValves.map { v -> v.valve.id }
                            .toSet()
                    if (possibleStepsElephant.isNotEmpty()) {
                        for (elephantOption in possibleStepsElephant) {
                            val reachableValves = pathsFromValves[it.currentValve.id] ?: emptyMap()
                            val possibleSteps =
                                reachableValves.keys.intersect(valvesWorthOpening) - it.openValves.map { v -> v.valve.id }
                                    .toSet() - elephantOption
                            val elephantPathSize =
                                reachableValvesElephant[elephantOption] ?: error("Can't reach option $elephantOption")
                            if (it.elephantMinute + elephantPathSize + 1 <= 26) {
                                val newElephantValve =
                                    valves[elephantOption] ?: error("Valve $elephantOption not found")
                                val openValves = it.openValves.toMutableList()
                                openValves.add(
                                    ValveOpening(
                                        newElephantValve,
                                        it.elephantMinute + elephantPathSize + 1,
                                        true
                                    )
                                )
                                if (possibleSteps.isNotEmpty()) {
                                    for (meOption in possibleSteps) {
                                        val newEValve = valves[meOption] ?: error("Valve $meOption not found")
                                        val allOpenValves = openValves.toMutableList()
                                        val pathSize =
                                            reachableValves[meOption] ?: error("Can't reach option $meOption")
                                        if (it.currentMinute + pathSize + 1 <= 26) {
                                            allOpenValves.add(
                                                ValveOpening(
                                                    newEValve,
                                                    it.currentMinute + pathSize + 1,
                                                    false
                                                )
                                            )
                                            if (exists(existingStates, State(allOpenValves))) {
                                                //  println("Skipping because tried this with elephant and human switched already")
                                            } else {
                                                existingStates.add(State(allOpenValves))
                                                options.addAll(
                                                    performPossibleMovesWithElephant(
                                                        valves,
                                                        valvesWorthOpening,
                                                        pathsFromValves,
                                                        setOf(
                                                            PairMovement(
                                                                it.currentMinute + pathSize + 1,
                                                                newEValve,
                                                                it.elephantMinute + elephantPathSize + 1,
                                                                newElephantValve,
                                                                allOpenValves
                                                            )
                                                        ),
                                                    )
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    if (exists(existingStates, State(openValves))) {
                                        //  println("Skipping because tried this with elephant and human switched already")
                                    } else {
                                        existingStates.add(State(openValves))
                                        options.addAll(
                                            performPossibleMovesWithElephant(
                                                valves,
                                                valvesWorthOpening,
                                                pathsFromValves,
                                                setOf(
                                                    PairMovement(
                                                        it.currentMinute,
                                                        it.currentValve,
                                                        it.elephantMinute + elephantPathSize + 1,
                                                        newElephantValve,
                                                        openValves
                                                    )
                                                ),
                                            )
                                        )
                                    }
                                }
                            } else {
                                val reachableValves = pathsFromValves[it.currentValve.id] ?: emptyMap()
                                val possibleSteps =
                                    reachableValves.keys.intersect(valvesWorthOpening) - it.openValves.map { v -> v.valve.id }
                                        .toSet()
                                for (option in possibleSteps) {
                                    val newValve = valves[option] ?: error("Valve $option not found")
                                    val openValves = it.openValves.toMutableList()
                                    val pathSize = reachableValves[option] ?: error("Can't reach option $option")
                                    if (it.currentMinute + pathSize + 1 <= 26) {
                                        openValves.add(ValveOpening(newValve, it.currentMinute + pathSize + 1, false))
                                        if (exists(existingStates, State(openValves))) {
                                            //  println("Skipping because tried this with elephant and human switched already")
                                        } else {
                                            existingStates.add(State(openValves))
                                            options.addAll(
                                                performPossibleMovesWithElephant(
                                                    valves,
                                                    valvesWorthOpening,
                                                    pathsFromValves,
                                                    setOf(
                                                        PairMovement(
                                                            it.currentMinute,
                                                            it.currentValve,
                                                            it.elephantMinute + elephantPathSize + 1,
                                                            newValve,
                                                            openValves
                                                        )
                                                    ),
                                                )
                                            )
                                        }
                                    }
                                }

                            }

                        }
                    }
                    options.toList()
                }
            }
        }
        return newMovements.flatten().distinctBy { it.openValves }.toSet()
    }

    private fun exists(existingStates: Set<State>, check: State): Boolean {
        return existingStates.any { existing ->
            existing.openings.map { "${it.valve.id}-${it.minute}" }
                .containsAll(check.openings.map { "${it.valve.id}-${it.minute}" })
        }
    }

    private fun generateStartingPossibilities(
        valvesWorthOpening: Set<String>,
        pathsFromValves: Map<String, Map<String, Int>>,
    ): List<List<ValveOpen>> {
        val startingMoves = valvesWorthOpening.map {
            val takes = pathsFromValves["AA"]!![it]!! + 1
            ValveOpen(it, takes)
        }

        return startingMoves.flatMap { startinValve ->
            (valvesWorthOpening - startinValve.valve).map {
                val takes = pathsFromValves["AA"]!![it]!! + 1
                listOf(startinValve, ValveOpen(it, takes))
            }
        }
    }

    private fun generatePossiblities(
        states: List<List<ValveOpen>>,
        valvesWorthOpening: Set<String>,
        pathsFromValves: Map<String, Map<String, Int>>,
    ): List<List<ValveOpen>> {
        val newStates = states.flatMap {
            (valvesWorthOpening - it.map { v -> v.valve }.toSet()).map { next ->
                val start = it[it.size - 2]
                val takes = pathsFromValves[start.valve]!![next]!! + 1
                val n = it.toMutableList()
                n.add(ValveOpen(next, start.minute + takes))
                n.toList()
            }
        }
        return newStates
    }

    data class State(
        val openings: List<ValveOpening>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as State

            if (!openings.map { "${it.valve.id}-${it.minute}" }.toSet()
                    .containsAll(other.openings.map { "${it.valve.id}-${it.minute}" }.toSet())
            ) return false

            return true
        }

        override fun hashCode(): Int {
            return openings.map { "${it.valve.id}-${it.minute}" }.sorted().joinToString("").hashCode()
        }
    }

    data class PairMovement(
        val currentMinute: Int,
        val currentValve: Valve,
        val elephantMinute: Int,
        val elephantValve: Valve,
        val openValves: List<ValveOpening>
    )

    data class ValveOpen(
        val valve: String,
        val minute: Int,
    )

    data class ValveOpening(
        val valve: Valve,
        val minute: Int,
        val elephant: Boolean,
    )

    data class Movement(
        val currentMinute: Int,
        val currentPressure: Long,
        val currentValve: Valve,
        val openValves: List<Valve>
    )

    data class Valve(
        val id: String,
        val pressure: Int,
        val reachableValves: List<String>,
    ) {
        override fun toString(): String {
            return "Valve(id='$id', pressure=$pressure)"
        }
    }
}