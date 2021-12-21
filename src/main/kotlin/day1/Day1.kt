package day1

import util.getInput

fun main() {
    val testInput = """
        199
        200
        208
        210
        200
        207
        240
        269
        260
        263
    """.trimIndent().split('\n')
    println(day1part1(testInput))
    println(day1part1(getInput(1)))
    println(day1part2(testInput))
    println(day1part2(getInput(1)))
}

fun day1part1(lines: List<String>): Int {
    return lines.map { it.toInt() }.zipWithNext().count { (a, b) -> b > a }
}

fun day1part2(lines: List<String>): Int {
    return lines.map { it.toInt() }.windowed(3).map { it.sum() }.zipWithNext().count { (a, b) -> b > a }
}