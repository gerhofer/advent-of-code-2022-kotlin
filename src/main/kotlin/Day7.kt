fun main(args: Array<String>) {
    println("Part 1: ${Day7.solvePart1()}")
    println("Part 2: ${Day7.solvePart2()}")
}

object Day7 {

    fun solvePart1(): Long {
        val input = Day7::class.java.getResource("day7.txt")?.readText() ?: error("Can't read input")
        val root = buildFileTree(input)
        val directoryToSize = getDirectoryWithSizes(root)
        return directoryToSize.values.filter { it <= 100000L }.sum()
    }

    private fun buildFileTree(input: String): Node {
        val root = Node("/", null, mutableListOf(), null)
        var currentNode = root
        input.split("\r\n")
            .drop(1)
            .forEach { line ->
                if (line == "$ ls") {
                    // don't do anything
                } else if (line.startsWith("$ cd")) {
                    val destination = line.substringAfter("$ cd ")
                    currentNode = if (destination == "..") {
                        currentNode.parent ?: error("Can't navigate level up, ${currentNode.name} has no parent")
                    } else {
                        currentNode.children.first { it.name == destination }
                    }
                } else {
                    val listing = line.split(" ")
                    if (listing[0] == "dir") {
                        currentNode.children.add(Node(listing[1], null, mutableListOf(), currentNode))
                    } else {
                        currentNode.children.add(Node(listing[1], listing[0].toLong(), mutableListOf(), currentNode))
                    }
                }
            }
        return root
    }


    private fun getDirectoryWithSizes(node: Node): MutableMap<String, Long> {
        val directoryToSize = mutableMapOf<String, Long>()
        directoryToSize[node.fullyQualifiedName()] = node.getSize()
        for (child in node.children.filter { it.isDirectory() }) {
            directoryToSize.putAll(getDirectoryWithSizes(child))
        }
        return directoryToSize
    }

    fun solvePart2(): Long {
        val input = Day7::class.java.getResource("day7.txt")?.readText() ?: error("Can't read input")
        val totalDiskSize = 70000000L
        val neededFreeSpace = 30000000L
        val root = buildFileTree(input)
        val directoryToSize = getDirectoryWithSizes(root)
        val availableSpace = totalDiskSize - (directoryToSize["/"] ?: error("root space notavailable"))
        val requiredSpace = neededFreeSpace - availableSpace
        return directoryToSize.values.filter { it >= requiredSpace }.min()
    }

}

class Node(
    val name: String,
    val size: Long?,
    val children: MutableList<Node>,
    val parent: Node?
) {
    fun getSize(): Long {
        return children.sumOf {
            it.size ?: it.getSize()
        }
    }

    fun isDirectory() : Boolean {
        return size == null
    }

    fun fullyQualifiedName(): String {
        var fullyQualifiedName = name
        var par = parent
        while (par?.name == "/") {
            fullyQualifiedName = par.name + "/" + fullyQualifiedName
            par = par.parent
        }
        return fullyQualifiedName
    }
}