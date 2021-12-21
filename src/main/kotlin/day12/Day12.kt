package day12

import util.getInput

fun main() {
    val testInput = """
fs-end
he-DX
fs-he
start-DX
pj-DX
end-zg
zg-sl
zg-pj
pj-he
RW-he
fs-DX
pj-RW
zg-RW
start-pj
he-WI
zg-he
pj-fs
start-RW
    """.trimIndent().split('\n')
    println(day12Part1(testInput))
    println(day12Part1(getInput(12)))
    println(day12Part2(testInput))
    println(day12Part2(getInput(12)))
}

fun day12Part1(lines: List<String>): Any {
    val system = CaveSystem(lines)
    system.dfs()
    return system.paths
}

fun day12Part2(lines: List<String>): Any {
    val system = CaveSystem(lines)
    system.dfsPart2()
    return system.paths
}

private class CaveSystem(lines: List<String>) {
    val caves = mutableMapOf<String, Cave>()

    var paths = 0

    init {
        for (line in lines) {
            val (a, b) = line.split('-')
            caves.computeIfAbsent(a, ::Cave)
            caves.computeIfAbsent(b, ::Cave)
            caves.getValue(a).connections.add(b)
            caves.getValue(b).connections.add(a)
        }
    }

    fun dfs(startCave: String = "start") {
        if (startCave == "end") {
            paths++
            return
        }
        val cave = caves.getValue(startCave)
        cave.visitedCount++
        for (connection in cave.connections) {
            val connectedCave = caves.getValue(connection)
            if (connectedCave.isSmall && connectedCave.visitedCount > 0) {
                continue
            }
            dfs(connection)
        }
        cave.visitedCount--
    }

    fun dfsPart2(startCave: String = "start") {
        if (startCave == "end") {
            paths++
            return
        }
        val cave = caves.getValue(startCave)
        cave.visitedCount++
        for (connection in cave.connections) {
            val connectedCave = caves.getValue(connection)
            if (connectedCave.isSmall && !connectedCave.canVisitForPart2(this)) {
                continue
            }
            dfsPart2(connection)
        }
        cave.visitedCount--
    }
}

private data class Cave(val name: String) {
    val connections = mutableSetOf<String>()
    var visitedCount = 0

    val isBig get() = name[0].isUpperCase()
    val isSmall get() = !isBig

    fun canVisitForPart2(system: CaveSystem): Boolean {
        return when {
            visitedCount == 0 -> true
            visitedCount >= 2 -> false
            else -> {
                if (name == "start" || name == "end") {
                    false
                } else {
                    val existing = system.caves.values.find { it.isSmall && it.visitedCount == 2 }
                    existing == null
                }
            }
        }
    }
}
