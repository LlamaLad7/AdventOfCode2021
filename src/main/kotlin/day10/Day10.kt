package day10

import util.getInput

fun main() {
    val testInput = """
        [({(<(())[]>[[{[]{<()<>>
        [(()[<>])]({[<{<<[]>>(
        {([(<{}[<>[]}>{[]{[(<()>
        (((({<>}<{<{<>}{[]{[]{}
        [[<[([]))<([[{}[[()]]]
        [{[{({}]{}}([{[{{{}}([]
        {<[[]]>}<{[{[{[]{()[[[]
        [<(<(<(<{}))><([]([]()
        <{([([[(<>()){}]>(<<{{
        <{([{{}}[<[[[<>{}]]]>[]]
    """.trimIndent().split('\n')
    println(day10Part1(testInput))
    println(day10Part1(getInput(10)))
    println(day10Part2(testInput))
    println(day10Part2(getInput(10)))
}

fun day10Part1(lines: List<String>): Any {
    var total = 0L
    for (line in lines) {
        val bad = line.firstBadChar() ?: continue
        total += scores[bad]!!
    }
    return total
}

private val scores = mapOf(
    ')' to 3, ']' to 57, '}' to 1197, '>' to 25137
)

private fun String.firstBadChar(): Char? {
    val stack = ArrayDeque<Char>()
    for (char in this) {
        if (char.closing != null) {
            stack.addFirst(char)
        } else {
            val top = stack.removeFirst()
            if (top.closing != char) {
                return char
            }
        }
    }
    return null
}

private val Char.closing get() = when (this) {
    '(' -> ')'
    '[' -> ']'
    '<' -> '>'
    '{' -> '}'
    else -> null
}

fun day10Part2(lines: List<String>): Any {
    val incompleteLines = lines.filter { it.firstBadChar() == null }
    val scores = mutableListOf<Long>()
    for (line in incompleteLines) {
        val completion = line.completionString()
        scores.add(completion.score())
    }
    return scores.sorted()[scores.size / 2]
}

private fun String.completionString(): String {
    val stack = ArrayDeque<Char>()
    for (char in this) {
        if (char.closing != null) {
            stack.addFirst(char)
        } else {
            val top = stack.removeFirst()
            if (top.closing != char) {
                error("Should've been filtered!")
            }
        }
    }
    return stack.map { it.closing }.joinToString("")
}

private fun String.score(): Long {
    var score = 0L
    for (char in this) {
        score *= 5
        score += completionScores[char]!!
    }
    return score
}

private val completionScores = mapOf(
    ')' to 1, ']' to 2, '}' to 3, '>' to 4
)