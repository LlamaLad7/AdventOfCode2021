package day11

import util.getInput

fun main() {
    val testInput = """
        5483143223
        2745854711
        5264556173
        6141336146
        6357385478
        4167524645
        2176841721
        6882881134
        4846848554
        5283751526
    """.trimIndent().split('\n')
    println(day11Part1(testInput))
    println(day11Part1(getInput(11)))
    println(day11Part2(testInput))
    println(day11Part2(getInput(11)))
}

fun day11Part1(lines: List<String>): Any {
    val octopuses = Octopuses(lines)
    var total = 0
    repeat(100) {
        octopuses.step()
        total += octopuses.countFlashesAndReset()
    }
    return total
}

fun day11Part2(lines: List<String>): Any {
    val octopuses = Octopuses(lines)
    var step = 1L
    while (true) {
        octopuses.step()
        if (octopuses.countFlashesAndReset() == 100) {
            return step
        }
        step++
    }
}

private class Octopus(var energy: Int) {
    var flashing = false
}

private class Octopuses(lines: List<String>) {

    private val rows = lines.map { line -> line.toList().map { Octopus(it.digitToInt()) } }

    fun step() {
        for ((rowIndex, row) in rows.withIndex()) {
            for ((cellIndex, cell) in row.withIndex()) {
                cell.energy++
                if (cell.energy > 9) {
                    propagateFlashes(rowIndex, cellIndex)
                }
            }
        }
    }

    fun propagateFlashes(y: Int, x: Int) {
        val row = rows[y]
        val cell = row[x]

        if (cell.flashing) return
        cell.energy++
        if (cell.energy <= 9) return
        cell.flashing = true

        val adjacents = mutableListOf(
            y - 1 to x, // Up
            y + 1 to x, // Down
            y to x - 1, // Left
            y to x + 1, // Right
            y - 1 to x - 1, // Up Left
            y - 1 to x + 1, // Up Right
            y + 1 to x - 1, // Down Left
            y + 1 to x + 1 // Down Right
        )
        adjacents.retainAll { it.first in rows.indices && it.second in row.indices }

        for ((newY, newX) in adjacents) {
            propagateFlashes(newY, newX)
        }
    }

    fun countFlashesAndReset(): Int {
        var total = 0
        for (row in rows) {
            for (cell in row) {
                if (cell.flashing) {
                    total++
                    cell.flashing = false
                    cell.energy = 0
                }
            }
        }
        return total
    }
}
