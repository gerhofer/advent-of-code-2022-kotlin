fun main(args: Array<String>) {
    //println("Part 1: ${Day16.solvePart1()}")
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

    fun solvePart2(): Long {
        val input = Day16::class.java.getResource("day16.txt")?.readText() ?: error("Can't read input")
        val valves = input.split("\r\n")
            .map { readValve(it) }
            .associateBy { it.id }

        val valvesWorthOpening = valves.values.filter { it.pressure > 0 }.map { it.id }.toSet()
        val pathsPerCave = valves.values.associate { it.id to getPathMap(valves, it) }
        val startingValve = valves["AA"] ?: error("Valve AA not found")
        val possibleMoves = performPossibleMovesWithElephant(
            valves,
            valvesWorthOpening,
            pathsPerCave,
            setOf(PairMovement(0,  startingValve, 0, startingValve, listOf()))
        )

        val withMax =  possibleMoves.maxBy { calculateTotalPressure(it.openValves) }
        println(withMax)
        return calculateTotalPressure(withMax.openValves)
    }

    private fun calculateTotalPressure(valveOpenings: List<ValveOpening>): Long {
        return valveOpenings.sumOf {
            it.valve.pressure.toLong() * (26 - it.minute)
        }
    }

    private fun performPossibleMovesWithElephant(
        valves: Map<String, Valve>,
        valvesWorthOpening: Set<String>,
        pathsFromValves: Map<String, Map<String, Int>>,
        movements: Set<PairMovement>
    ): Set<PairMovement> {
        val newMovements = movements.map {
            if (it.currentMinute >= 26 && it.elephantMinute >= 26 || it.openValves.size == valvesWorthOpening.size) {
                setOf(it)
            } else {
                val options = mutableListOf<PairMovement>()
                val reachableValvesElephant = pathsFromValves[it.elephantValve.id] ?: emptyMap()
                val possibleStepsElephant = reachableValvesElephant.keys.intersect(valvesWorthOpening) - it.openValves.map { v -> v.valve.id }.toSet()
                if (possibleStepsElephant.isNotEmpty()) {
                    for (elephantOption in possibleStepsElephant) {
                        val reachableValves = pathsFromValves[it.currentValve.id] ?: emptyMap()
                        val possibleSteps = reachableValves.keys.intersect(valvesWorthOpening) - it.openValves.map { v -> v.valve.id }.toSet() - elephantOption
                        val elephantPathSize = reachableValvesElephant[elephantOption] ?: error("Can't reach option $elephantOption")
                        if (it.elephantMinute + elephantPathSize + 1 <= 26) {
                            val newElephantValve = valves[elephantOption] ?: error("Valve $elephantOption not found")
                            val openValves = it.openValves.toMutableList()
                            openValves.add(ValveOpening(newElephantValve, it.elephantMinute + elephantPathSize + 1, true))
                            if (possibleSteps.isNotEmpty()) {
                                for (meOption in possibleSteps) {
                                    val newEValve = valves[meOption] ?: error("Valve $meOption not found")
                                    val allOpenValves = openValves.toMutableList()
                                    val pathSize = reachableValves[meOption] ?: error("Can't reach option $meOption")
                                    if (it.currentMinute + pathSize + 1 <= 26) {
                                        allOpenValves.add(ValveOpening(newEValve, it.currentMinute + pathSize + 1, false))
                                        options.addAll(
                                            performPossibleMovesWithElephant(
                                                valves,
                                                valvesWorthOpening,
                                                pathsFromValves,
                                                setOf(
                                                    PairMovement(
                                                        it.currentMinute + pathSize + 1 ,
                                                        newEValve,
                                                        it.elephantMinute + elephantPathSize + 1,
                                                        newElephantValve,
                                                        allOpenValves
                                                    )
                                                )
                                            )
                                        )
                                    }
                                }
                            } else {
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
                                        )
                                    )
                                )
                            }
                        } else {
                            val reachableValves = pathsFromValves[it.currentValve.id] ?: emptyMap()
                            val possibleSteps = reachableValves.keys.intersect(valvesWorthOpening) - it.openValves.map { v -> v.valve.id }.toSet()
                            for (option in possibleSteps) {
                                val newValve = valves[option] ?: error("Valve $option not found")
                                val openValves = it.openValves.toMutableList()
                                val pathSize = reachableValves[option] ?: error("Can't reach option $option")
                                if (it.currentMinute + pathSize + 1 <= 26) {
                                    openValves.add(ValveOpening(newValve, it.currentMinute + pathSize + 1, false))
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
                                            )
                                        )
                                    )
                                }
                            }

                        }
                    }
                }
                options.toList()
            }
        }
        return newMovements.flatten().toSet()
    }

    data class PairMovement(
        val currentMinute: Int,
        val currentValve: Valve,
        val elephantMinute: Int,
        val elephantValve: Valve,
        val openValves: List<ValveOpening>
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