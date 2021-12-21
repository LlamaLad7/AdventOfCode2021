package aoc2021.day2

import util.getInput

fun main() {
    val testInput = """
        forward 5
        down 5
        forward 8
        up 3
        down 8
        forward 2
    """.trimIndent().split('\n')
    println(day2part1(testInput))
    println(day2part1(getInput(2)))
    println(day2part2(testInput))
    println(day2part2(getInput(2)))
}

fun day2part1(lines: List<String>): Int {
    return lines
        .map { it.split(' ').let { (a, b) -> a to b.toInt() } }
        .groupBy { it.first }
        .mapValues { entry -> entry.value.sumOf { it.second } }
        .let { it["forward"]!! * (it["down"]!! - it["up"]!!) }
}

fun day2part2(lines: List<String>): Int {
    var aim = 0
    var depth = 0
    var pos = 0
    for ((command, str) in lines.map { it.split(' ') }) {
        val num = str.toInt()
        when (command) {
            "down" -> aim += num
            "up" -> aim -= num
            "forward" -> {
                pos += num
                depth += aim * num
            }
        }
    }
    return depth * pos
}