package day14

import util.getInput
import java.math.BigInteger

fun main() {
    val testInput = """
        NNCB

        CH -> B
        HH -> N
        CB -> H
        NH -> C
        HB -> C
        HC -> B
        HN -> C
        NN -> C
        BH -> H
        NC -> B
        NB -> B
        BN -> B
        BB -> N
        BC -> B
        CC -> N
        CN -> C
    """.trimIndent().split('\n')
    println(day14Part1(testInput))
    println(day14Part1(getInput(14)))
    println(day14Part2(testInput))
    println(day14Part2(getInput(14)))
}

fun day14Part1(lines: List<String>): Any {
    var (string, replacements) = parseInput(lines)
    repeat(10) {
        string = string.takeStep(replacements)
    }
    val chars = string.toSet()
    return chars.maxOf { char -> string.count { it == char } } - chars.minOf { char -> string.count { it == char } }
}

private fun String.takeStep(replacements: Map<String, Char>) = buildString {
    for (pair in this@takeStep.windowed(2)) {
        append(pair[0])
        replacements[pair]?.let { append(it) }
    }
    append(this@takeStep.last())
}

fun day14Part2(lines: List<String>): Any {
    val (string, replacements) = parseInput(lines)
    val pairCounts = mutableMapOf<String, BigInteger>()
    for (pair in string.windowed(2)) {
        pairCounts.putIfAbsent(pair, BigInteger.ZERO)
        pairCounts[pair] = pairCounts.getValue(pair) + BigInteger.ONE
    }

    repeat(40) {
        for ((pair, count) in pairCounts.entries.map { it.key to it.value }) {
            val replacement = replacements[pair] ?: continue
            val left = "${pair[0]}$replacement"
            val right = "$replacement${pair[1]}"
            pairCounts.putIfAbsent(left, BigInteger.ZERO)
            pairCounts.putIfAbsent(right, BigInteger.ZERO)

            pairCounts[pair] = pairCounts.getValue(pair) - count
            pairCounts[left] = pairCounts.getValue(left) + count
            pairCounts[right] = pairCounts.getValue(right) + count
        }
    }

    val charCounts = mutableMapOf<Char, BigInteger>()
    for ((pair, count) in pairCounts) {
        val leftChar = pair[0]
        charCounts.putIfAbsent(leftChar, BigInteger.ZERO)
        charCounts[leftChar] = charCounts.getValue(leftChar) + count
    }
    charCounts[string.last()] = charCounts.getValue(string.last()) + BigInteger.ONE
    return charCounts.maxOf { it.value } - charCounts.minOf { it.value }
}

private fun parseInput(lines: List<String>): Pair<String, Map<String, Char>> {
    val string = lines[0]
    val replacements = lines.drop(2).associate { it.split(" -> ").let { (a, b) -> a to b.first() } }
    return string to replacements
}