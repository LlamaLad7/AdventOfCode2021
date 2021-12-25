package day25

import util.getInput

fun main() {
    val testInput = """
            v...>>.vv>
            .vv>>.vv..
            >>.>v>...v
            >>v>>.>.v.
            v>v.vv.v..
            >.>>..v...
            .vv..>.>v.
            v.v..>>v.v
            ....v..v.>
    """.trimIndent().split('\n')
    println(day25Part1(testInput))
    println(day25Part1(getInput(25)))
}

fun day25Part1(lines: List<String>): Any {
    val map = SeaMap(lines)
    var step = 1
    while (true) {
        val moved = map.advanceEast() or map.advanceSouth()
        if (!moved) {
            return step
        }
        step++
    }
}

private class SeaMap(lines: List<String>) {
    private var herd = lines.map { it.toCharArray() }.toTypedArray()

    fun advanceEast(): Boolean {
        val newHerd = herd.map { row -> row.map { if (it == '>') '.' else it }.toCharArray() }.toTypedArray()
        var advanced = false
        for ((rowIndex, row) in herd.withIndex()) {
            for ((cellIndex, cell) in row.withIndex()) {
                if (cell == '>') {
                    val targetX = (cellIndex + 1) % newHerd[0].size
                    if (herd[rowIndex][targetX] == '.') {
                        newHerd[rowIndex][targetX] = '>'
                        advanced = true
                    } else {
                        newHerd[rowIndex][cellIndex] = '>'
                    }
                }
            }
        }
        herd = newHerd
        return advanced
    }

    fun advanceSouth(): Boolean {
        val newHerd = herd.map { row -> row.map { if (it == 'v') '.' else it }.toCharArray() }.toTypedArray()
        var advanced = false
        for ((rowIndex, row) in herd.withIndex()) {
            for ((cellIndex, cell) in row.withIndex()) {
                if (cell == 'v') {
                    val targetY = (rowIndex + 1) % newHerd.size
                    if (herd[targetY][cellIndex] == '.') {
                        newHerd[targetY][cellIndex] = 'v'
                        advanced = true
                    } else {
                        newHerd[rowIndex][cellIndex] = 'v'
                    }
                }
            }
        }
        herd = newHerd
        return advanced
    }

    override fun toString() = herd.joinToString("\n") { it.joinToString("") }
}