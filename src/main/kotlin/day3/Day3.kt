package day3

import util.getInput

fun main() {
    println(
        day3Part1("""
        00100
        11110
        10110
        10111
        10101
        01111
        00111
        11100
        10000
        11001
        00010
        01010
    """.trimIndent().split('\n'))
    )
    println(day3Part1(getInput(3)))
    println(
        day3Part2("""
        00100
        11110
        10110
        10111
        10101
        01111
        00111
        11100
        10000
        11001
        00010
        01010
    """.trimIndent().split('\n'))
    )
    println(day3Part2(getInput(3)))
}

fun day3Part1(lines: List<String>): Any {
    return buildString {
        for (i in lines[0].indices) {
            append(charArrayOf('0', '1').maxByOrNull { c ->
                lines.map { it[i] }.count { it == c }
            })
        }
    }.toInt(2).let { it * (it xor ((1 shl lines[0].length) - 1)) }
}

fun day3Part2(lines: List<String>): Any {
    return getOxygenRating(lines) * getCO2Rating(lines)
}

private fun getOxygenRating(lines: List<String>): Int {
    val l = lines.toMutableList()
    for (i in lines[0].indices) {
        val ones = l.count { it[i] == '1' }
        val zeros = l.size - ones
        val desired = if (ones < zeros) '0' else '1'
        l.removeIf { it[i] != desired }
        if (l.size == 1) return l[0].toInt(2)
    }
    return -1
}

private fun getCO2Rating(lines: List<String>): Int {
    val l = lines.toMutableList()
    for (i in lines[0].indices) {
        val ones = l.count { it[i] == '1' }
        val zeros = l.size - ones
        val desired = if (ones < zeros) '1' else '0'
        l.removeIf { it[i] != desired }
        if (l.size == 1) return l[0].toInt(2)
    }
    return -1
}