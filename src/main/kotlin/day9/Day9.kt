package day9

import util.getInput

fun main() {
    val testInput = """
        2199943210
        3987894921
        9856789892
        8767896789
        9899965678
    """.trimIndent().split('\n')
    println(day9Part1(testInput))
    println(day9Part1(getInput(9)))
    println(day9Part2(testInput))
    println(day9Part2(getInput(9)))
}

fun day9Part1(lines: List<String>): Any {
    var total = 0L
    for ((rowIndex, row) in lines.withIndex()) {
        for ((cellIndex, cell) in row.map { it.digitToInt() }.withIndex()) {
            val adjacents = mutableListOf(rowIndex - 1 to cellIndex, rowIndex + 1 to cellIndex, rowIndex to cellIndex - 1, rowIndex to cellIndex + 1)
            adjacents.retainAll { it.first in lines.indices && it.second in row.indices }
            if (adjacents.all { (y, x) -> lines[y][x].digitToInt() > cell }) {
                total += cell + 1
            }
        }
    }
    return total
}

fun day9Part2(lines: List<String>): Any {
    val system = CaveSystem(lines)
    val basinSizes = mutableListOf<Int>()
    for ((rowIndex, row) in lines.withIndex()) {
        for ((cellIndex, cell) in row.map { it.digitToInt() }.withIndex()) {
            val adjacents = mutableListOf(rowIndex - 1 to cellIndex, rowIndex + 1 to cellIndex, rowIndex to cellIndex - 1, rowIndex to cellIndex + 1)
            adjacents.retainAll { it.first in lines.indices && it.second in row.indices }
            if (adjacents.all { (y, x) -> lines[y][x].digitToInt() > cell }) {
                basinSizes.add(system.findBasinSize(rowIndex, cellIndex))
            }
        }
    }
    return basinSizes.sorted().takeLast(3).let { (a, b, c) -> a * b * c }
}

private class Cave(val height: Int) {
    var filled = false
}

private class CaveSystem(lines: List<String>) {
    private val rows = lines.map { line -> line.toList().map { Cave(it.digitToInt()) } }

    fun findBasinSize(y: Int, x: Int): Int {
        val row = rows[y]
        val cell = row[x]
        if (cell.filled || cell.height == 9) return 0

        var total = 1
        cell.filled = true

        val adjacents = mutableListOf(y - 1 to x, y + 1 to x, y to x - 1, y to x + 1)
        adjacents.retainAll { it.first in rows.indices && it.second in row.indices }

        for ((newY, newX) in adjacents) {
            total += findBasinSize(newY, newX)
        }

        return total
    }
}