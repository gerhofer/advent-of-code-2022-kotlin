fun main(args: Array<String>) {
    // println("Part 1: ${Day22.solvePart1()}")
    println("Part 2: ${Day22.solvePart2()}")
}

object Day22 {

    const val NOTHING = " "
    const val WALL = "#"
    const val OPEN = "."

    fun solvePart1(): Long {
        val input = Day22::class.java.getResource("day22.txt")?.readText() ?: error("Can't read input")
        val areas = input.split("\r\n\r\n")
        val field = areas[0].split("\r\n")
            .map { it.split("").filter { c -> c.isNotEmpty() } }
        var path = areas[1]

        val startingColumn = field[0].indexOfFirst { it == OPEN }
        val position = Position(0, startingColumn)
        var direction = Direction.RIGHT

        while (path.isNotBlank()) {
            val beforeR = path.substringBefore("R")
            val beforeL = path.substringBefore("L")
            if (beforeL.length < beforeR.length && beforeL != path) {
                // next is L
                val amount = beforeL.toInt()
                move(position, field, direction, amount)
                direction = direction.turnCounterClockwise()
                path = path.substring(beforeL.length + 1)
            } else if (beforeR != path) {
                // next is R
                val amount = beforeR.toInt()
                move(position, field, direction, amount)
                direction = direction.turnClockwise()
                path = path.substring(beforeR.length + 1)
            } else {
                val amount = path.toInt()
                move(position, field, direction, amount)
                path = ""
            }
            //println("Path is $path, position is $position, direction is $direction")
        }

        return 1000L * (position.row + 1) + 4 * (position.column + 1) + direction.value
    }

    fun move(position: Position, field: List<List<String>>, direction: Direction, stepSize: Int): Position {
        when (direction) {
            Direction.UP -> {
                repeat(stepSize) { position.moveUp(field) }
            }

            Direction.RIGHT -> {
                repeat(stepSize) { position.moveRight(field) }
            }

            Direction.DOWN -> {
                repeat(stepSize) { position.moveDown(field) }
            }

            Direction.LEFT -> {
                repeat(stepSize) { position.moveLeft(field) }
            }
        }
        return position
    }

    fun solvePart2(): Long {
        val input = Day22::class.java.getResource("day22.txt")?.readText() ?: error("Can't read input")
        val areas = input.split("\r\n\r\n")
        val field = areas[0].split("\r\n")
            .map { it.split("").filter { c -> c.isNotBlank() } }
        val height = field.size / 3
        val width = field[0].size
        val cube = Cube(
            field.subList(0, height),
            field.subList(height, height * 2).map { it.subList(0, width) },
            field.subList(height, height * 2).map { it.subList(width, width * 2) },
            field.subList(height, height * 2).map { it.subList(width * 2, width * 3) },
            field.subList(height * 2, height * 3).map { it.subList(0, width) },
            field.subList(height * 2, height * 3).map { it.subList(width, width * 2) },
        )
        var position = CubePosition(CubeFace.FIRST, 0, 0, Direction.RIGHT)
        var path = areas[1]

        while (path.isNotBlank()) {
            val beforeR = path.substringBefore("R")
            val beforeL = path.substringBefore("L")
            if (beforeL.length < beforeR.length && beforeL != path) {
                // next is L
                val amount = beforeL.toInt()
                move(position, cube, amount)
                position.direction = position.direction.turnCounterClockwise()
                path = path.substring(beforeL.length + 1)
            } else if (beforeR != path) {
                // next is R
                val amount = beforeR.toInt()
                move(position, cube, amount)
                position.direction = position.direction.turnClockwise()
                path = path.substring(beforeR.length + 1)
            } else {
                val amount = path.toInt()
                move(position, cube, amount)
                path = ""
            }
            println("Path is $path, position is $position, direction is ${position.direction}")
        }

        return when (position.face) {
            CubeFace.FIRST -> 1000L * (position.row + 1) + 4 * (position.column + 2 * width + 1) + position.direction.value
            CubeFace.SECOND -> 1000L * (position.row + width + 1) + 4 * (position.column + 1) + position.direction.value
            CubeFace.THIRD -> 1000L * (position.row + width + 1) + 4 * (position.column + width + 1) + position.direction.value
            CubeFace.FOURTH -> 1000L * (position.row + width + 1) + 4 * (position.column + 2 * width + 1) + position.direction.value
            CubeFace.FIFTH -> 1000L * (position.row + 2 * width + 1) + 4 * (position.column + 2 * width + 1) + position.direction.value
            CubeFace.SIXTH -> 1000L * (position.row + 2 * width + 1) + 4 * (position.column + 3 * width + 1) + position.direction.value
        }
    }

    fun move(position: CubePosition, cube: Cube, stepSize: Int): CubePosition {
        repeat(stepSize) { position.move(cube) }
        return position
    }

    data class CubePosition(
        var face: CubeFace,
        var row: Int,
        var column: Int,
        var direction: Direction,
    ) {

        fun move(cube: Cube) {
            when (direction) {
                Direction.UP -> moveUp(cube)
                Direction.RIGHT -> moveRight(cube)
                Direction.DOWN -> moveDown(cube)
                Direction.LEFT -> moveLeft(cube)
            }
        }

        fun moveUp(cube: Cube) {
            val field = cube.getFace(face)
            if (row > 0) {
                val rowAbove = field[row - 1][column]
                if (rowAbove == OPEN) {
                    this.row--
                } else if (rowAbove == WALL) {
                    // do nothing
                } else {
                    error("Field must only contain walls and open spots $this wanted to move UP")
                }
            } else {
                when (face) {
                    CubeFace.FIRST -> {
                        val newFace = cube.getFace(CubeFace.SECOND)
                        val newRow = 0
                        val newColumn = (newFace.size - 1) - column
                        val newDirection = Direction.DOWN
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.SECOND
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.SECOND -> {
                        val newFace = cube.getFace(CubeFace.FIRST)
                        val newRow = 0
                        val newColumn = (newFace.size - 1) - column
                        val newDirection = Direction.DOWN
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.FIRST
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.THIRD -> {
                        val newFace = cube.getFace(CubeFace.FIRST)
                        val newRow = column
                        val newColumn = 0
                        val newDirection = Direction.RIGHT
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.FIRST
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.FOURTH -> {
                        val newFace = cube.getFace(CubeFace.FIRST)
                        val newRow = newFace.size - 1
                        val newColumn = column
                        val newDirection = Direction.UP
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.FIRST
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.FIFTH -> {
                        val newFace = cube.getFace(CubeFace.FOURTH)
                        val newRow = newFace.size - 1
                        val newColumn = column
                        val newDirection = Direction.UP
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.FOURTH
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.SIXTH -> {
                        val newFace = cube.getFace(CubeFace.FOURTH)
                        val newRow = (newFace.size - 1) - column
                        val newColumn = newFace.size - 1
                        val newDirection = Direction.LEFT
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.FOURTH
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }
                }
            }
        }

        fun moveDown(cube: Cube) {
            val field = cube.getFace(face)
            if (row < field.size - 1) {
                val rowBelow = field[row + 1][column]
                if (rowBelow == OPEN) {
                    this.row++
                } else if (rowBelow == WALL) {
                    // do nothing
                } else {
                    error("Field must only contain walls and open spots $this wanted to move DOWN")
                }
            } else {
                when (face) {
                    CubeFace.FIRST -> {
                        val newFace = cube.getFace(CubeFace.FOURTH)
                        val newRow = 0
                        val newColumn = column
                        val newDirection = Direction.DOWN
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.FOURTH
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.SECOND -> {
                        val newFace = cube.getFace(CubeFace.FIFTH)
                        val newRow = newFace.size - 1
                        val newColumn = (newFace.size - 1) - column
                        val newDirection = Direction.UP
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.FIFTH
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.THIRD -> {
                        val newFace = cube.getFace(CubeFace.FIFTH)
                        val newRow = (newFace.size - 1) - column
                        val newColumn = 0
                        val newDirection = Direction.RIGHT
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.FIFTH
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.FOURTH -> {
                        val newFace = cube.getFace(CubeFace.FIFTH)
                        val newRow = 0
                        val newColumn = column
                        val newDirection = Direction.DOWN
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.FIFTH
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.FIFTH -> {
                        val newFace = cube.getFace(CubeFace.SECOND)
                        val newRow = newFace.size - 1
                        val newColumn = (newFace.size - 1) - column
                        val newDirection = Direction.UP
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.SECOND
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.SIXTH -> {
                        val newFace = cube.getFace(CubeFace.SECOND)
                        val newRow = (newFace.size - 1) - column
                        val newColumn = 0
                        val newDirection = Direction.RIGHT
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.SECOND
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }
                }
            }
        }

        fun moveLeft(cube: Cube) {
            val field = cube.getFace(face)
            if (column > 0) {
                val columnLeft = field[row][column - 1]
                if (columnLeft == OPEN) {
                    this.column--
                } else if (columnLeft == WALL) {
                    // do nothing
                } else {
                    error("Field must only contain walls and open spots $this wanted to move DOWN")
                }
            } else {
                when (face) {
                    CubeFace.FIRST -> {
                        val newFace = cube.getFace(CubeFace.THIRD)
                        val newRow = 0
                        val newColumn = row
                        val newDirection = Direction.DOWN
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.THIRD
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.SECOND -> {
                        val newFace = cube.getFace(CubeFace.SIXTH)
                        val newRow = newFace.size - 1
                        val newColumn = (newFace.size - 1) - row
                        val newDirection = Direction.UP
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.SIXTH
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.THIRD -> {
                        val newFace = cube.getFace(CubeFace.SECOND)
                        val newRow = row
                        val newColumn = newFace.size - 1
                        val newDirection = Direction.LEFT
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.SECOND
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.FOURTH -> {
                        val newFace = cube.getFace(CubeFace.THIRD)
                        val newRow = row
                        val newColumn = newFace.size - 1
                        val newDirection = Direction.LEFT
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.THIRD
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.FIFTH -> {
                        val newFace = cube.getFace(CubeFace.THIRD)
                        val newRow = newFace.size - 1
                        val newColumn = (newFace.size - 1) - row
                        val newDirection = Direction.UP
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.THIRD
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.SIXTH -> {
                        val newFace = cube.getFace(CubeFace.FIFTH)
                        val newRow = row
                        val newColumn = newFace.size - 1
                        val newDirection = Direction.LEFT
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.FIFTH
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }
                }
            }
        }

        fun moveRight(cube: Cube) {
            val field = cube.getFace(face)
            if (column < field.size - 1) {
                val columnRightW = field[row]
                val columnRight = columnRightW[column + 1]
                if (columnRight == OPEN) {
                    this.column++
                } else if (columnRight == WALL) {
                    // do nothing
                } else {
                    error("Field must only contain walls and open spots $this wanted to move RIGHT")
                }
            } else {
                when (face) {
                    CubeFace.FIRST -> {
                        val newFace = cube.getFace(CubeFace.SIXTH)
                        val newRow = newFace.size - 1
                        val newColumn = (newFace.size - 1) - row
                        val newDirection = Direction.LEFT
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.SIXTH
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.SECOND -> {
                        val newFace = cube.getFace(CubeFace.THIRD)
                        val newRow = row
                        val newColumn = 0
                        val newDirection = Direction.RIGHT
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.THIRD
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.THIRD -> {
                        val newFace = cube.getFace(CubeFace.FOURTH)
                        val newRow = row
                        val newColumn = 0
                        val newDirection = Direction.RIGHT
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.FOURTH
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.FOURTH -> {
                        val newFace = cube.getFace(CubeFace.SIXTH)
                        val newRow = 0
                        val newColumn = (newFace.size - 1) - row
                        val newDirection = Direction.DOWN
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.SIXTH
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.FIFTH -> {
                        val newFace = cube.getFace(CubeFace.SIXTH)
                        val newRow = row
                        val newColumn = 0
                        val newDirection = Direction.RIGHT
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.SIXTH
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }

                    CubeFace.SIXTH -> {
                        val newFace = cube.getFace(CubeFace.FIRST)
                        val newRow = (newFace.size - 1) - column
                        val newColumn = newFace.size - 1
                        val newDirection = Direction.LEFT
                        if (newFace[newRow][newColumn] == OPEN) {
                            face = CubeFace.FIRST
                            row = newRow
                            column = newColumn
                            direction = newDirection
                        }
                    }
                }
            }
        }

    }

    data class Position(
        var row: Int,
        var column: Int
    ) {
        fun moveUp(field: List<List<String>>) {
            if (row > 0) {
                val rowAbove = field[row - 1][column]
                if (rowAbove == OPEN) {
                    this.row--
                } else if (rowAbove == WALL) {
                    // do nothing
                } else {
                    // move to first from bottom
                    val lastFilledField = field.map { it.getOrNull(column) ?: NOTHING }.indexOfLast { it != NOTHING }
                    if (field[lastFilledField][column] == OPEN) {
                        this.row = lastFilledField
                    }
                }
            } else {
                // move to first from bottom
                val lastFilledField = field.map { it.getOrNull(column) ?: NOTHING }.indexOfLast { it != NOTHING }
                if (field[lastFilledField][column] == OPEN) {
                    this.row = lastFilledField
                }
            }
        }

        fun moveDown(field: List<List<String>>) {
            if (row < field.size - 1) {
                val rowBelowI = field[row + 1]
                val rowBelow = rowBelowI.getOrNull(column) ?: NOTHING
                if (rowBelow == OPEN) {
                    this.row++
                } else if (rowBelow == WALL) {
                    // do nothing
                } else {
                    // move to first from top
                    val lastFilledField = field.map { it.getOrNull(column) ?: NOTHING }.indexOfFirst { it != NOTHING }
                    if (field[lastFilledField][column] == OPEN) {
                        this.row = lastFilledField
                    }
                }
            } else {
                // move to first from top
                val lastFilledField = field.map { it.getOrNull(column) ?: NOTHING }.indexOfFirst { it != NOTHING }
                if (field[lastFilledField][column] == OPEN) {
                    this.row = lastFilledField
                }
            }
        }

        fun moveLeft(field: List<List<String>>) {
            if (column > 0) {
                val columnLeft = field[row].getOrNull(column - 1) ?: NOTHING
                if (columnLeft == OPEN) {
                    this.column--
                } else if (columnLeft == WALL) {
                    // do nothing
                } else {
                    // move to first from right
                    val lastFilledField = field[row].indexOfLast { it != NOTHING }
                    if (field[row][lastFilledField] == OPEN) {
                        this.column = lastFilledField
                    }
                }
            } else {
                // move to first from right
                val lastFilledField = field[row].indexOfLast { it != NOTHING }
                if (field[row][lastFilledField] == OPEN) {
                    this.column = lastFilledField
                }
            }
        }

        fun moveRight(field: List<List<String>>) {
            if (column < field[row].size - 1) {
                val columnRight = field[row][column + 1]
                if (columnRight == OPEN) {
                    this.column++
                } else if (columnRight == WALL) {
                    // do nothing
                } else {
                    // move to first from left
                    val lastFilledField = field[row].indexOfFirst { it != NOTHING }
                    if (field[row][lastFilledField] == OPEN) {
                        this.column = lastFilledField
                    }
                }
            } else {
                // move to first from left
                val lastFilledField = field[row].indexOfFirst { it != NOTHING }
                if (field[row][lastFilledField] == OPEN) {
                    this.column = lastFilledField
                }
            }
        }
    }

    data class Cube(
        val face1: List<List<String>>,
        val face2: List<List<String>>,
        val face3: List<List<String>>,
        val face4: List<List<String>>,
        val face5: List<List<String>>,
        val face6: List<List<String>>
    ) {
        fun getFace(face: CubeFace): List<List<String>> {
            return when (face) {
                CubeFace.FIRST -> face1
                CubeFace.SECOND -> face2
                CubeFace.THIRD -> face3
                CubeFace.FOURTH -> face4
                CubeFace.FIFTH -> face5
                CubeFace.SIXTH -> face6
            }
        }
    }

    enum class CubeFace {
        FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH

        /*fun getUp() {
            return when (this) {
                FIRST -> SECOND
                SECOND -> FIFTH
                THIRD -> LEFT
                FOURTH -> UP
                FIFTH ->
                    SIXTH ->
            }
        }

        fun getDown() {
            return when (this) {
                FIRST -> FOURTH
                SECOND -> FIRST
                THIRD -> LEFT
                FOURTH -> UP
                FIFTH ->
                    SIXTH ->
            }
        }

        fun getLeft() {
            return when (this) {
                FIRST -> SIXTH
                SECOND -> SIXTH
                THIRD -> LEFT
                FOURTH -> UP
                FIFTH ->
                    SIXTH ->
            }
        }

        fun getRight() {
            return when (this) {
                FIRST -> THIRD
                SECOND -> DOWN
                THIRD -> LEFT
                FOURTH -> UP
                FIFTH ->
                    SIXTH ->
            }
        }*/
    }

    enum class Direction(val value: Int) {
        UP(3), DOWN(1), LEFT(2), RIGHT(0);

        fun turnClockwise(): Direction {
            return when (this) {
                UP -> RIGHT
                RIGHT -> DOWN
                DOWN -> LEFT
                LEFT -> UP
            }
        }

        fun turnCounterClockwise(): Direction {
            return when (this) {
                UP -> LEFT
                RIGHT -> UP
                DOWN -> RIGHT
                LEFT -> DOWN
            }
        }
    }

}