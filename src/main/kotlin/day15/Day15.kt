package day15

import util.getInput
import java.util.*

fun main() {
    val testInput = """
        1163751742
        1381373672
        2136511328
        3694931569
        7463417111
        1319128137
        1359912421
        3125421639
        1293138521
        2311944581
    """.trimIndent().split('\n')
    println(day15Part1(testInput))
    println(day15Part1(getInput(15)))
    println(day15Part2(testInput))
    println(day15Part2(getInput(15)))
}

fun day15Part1(lines: List<String>): Any {
    val matrix = lines.map { line -> line.map { it.digitToInt() } }
    return dijkstra(matrix)
}

fun day15Part2(lines: List<String>): Any {
    var tile = lines.map { line -> line.map { it.digitToInt() } }
    var row = tile.map { it.toMutableList() }.toMutableList()
    repeat(4) {
        tile = tile.addOneToAllItems()
        row.addRight(tile)
    }

    val board = row.map { it.toMutableList() }.toMutableList()
    repeat(4) {
        row = row.addOneToAllItems().map { it.toMutableList() }.toMutableList()
        board.addDown(row)
    }

    return dijkstra(board)
}

private fun List<List<Int>>.addOneToAllItems(): List<List<Int>> {
    return this.map { row -> row.map { it % 9 + 1 } }
}

private fun List<MutableList<Int>>.addRight(other: List<List<Int>>) {
    for ((rowIndex, row) in other.withIndex()) {
        this[rowIndex] += row
    }
}

private fun MutableList<MutableList<Int>>.addDown(other: MutableList<MutableList<Int>>) {
    this += other
}

private data class Coordinate(val x: Int, val y: Int)

private data class PriorityCoordinate(val priority: Int, val coordinate: Coordinate) : Comparable<PriorityCoordinate> {
    override operator fun compareTo(other: PriorityCoordinate) = priority.compareTo(other.priority)
}

private fun dijkstra(matrix: List<List<Int>>): Int {
    val queue = PriorityQueue<PriorityCoordinate>()
    val dist = mutableMapOf<Coordinate, Int>()
    for (i in matrix.indices) {
        for (j in matrix[0].indices) {
            dist[Coordinate(i, j)] = Int.MAX_VALUE
        }
    }

    val source = Coordinate(0, 0)
    dist[source] = 0
    queue.add(PriorityCoordinate(0, source))

    while (queue.isNotEmpty()) {
        val (priority, coord) = queue.remove()
        if (priority != dist[coord]) continue
        val (x, y) = coord
        val adjacents = mutableListOf(
            Coordinate(x + 1, y), // Right
            Coordinate(x, y + 1), // Down
            Coordinate(x - 1, y), // Left
            Coordinate(x, y - 1), // Up
        )
        adjacents.retainAll { it.x in matrix.indices && it.y in matrix[0].indices }

        for (adjacent in adjacents) {
            val newDist = dist.getValue(coord) + matrix[adjacent.y][adjacent.x]
            if (newDist < dist.getValue(adjacent)) {
                dist[adjacent] = newDist
                queue.add(PriorityCoordinate(newDist, adjacent))
            }
        }
    }

    return dist.getValue(Coordinate(matrix.indices.last, matrix[0].indices.last))
}