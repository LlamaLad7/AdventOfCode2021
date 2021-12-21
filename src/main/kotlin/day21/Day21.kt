package day21

import com.aballano.mnemonik.memoize
import util.getInput
import java.math.BigInteger

fun main() {
    val testInput = """
        Player 1 starting position: 4
        Player 2 starting position: 8
    """.trimIndent().split('\n')
    println(day21Part1(testInput))
    println(day21Part1(getInput(21)))
    println(day21Part2(testInput))
    println(day21Part2(getInput(21)))
}

private interface Die {
    fun roll(): Int
}

private class DeterministicDie : Die {
    var counter = 0

    private var state = 1

    override fun roll(): Int = state.also {
        state = state % 100 + 1
        counter++
    }
}

private class Player(var pos: Int) {
    var score = 0

    fun move(die: Die) {
        val moveBy = die.roll() + die.roll() + die.roll()
        pos = (pos + moveBy - 1) % 10 + 1
        score += pos
    }
}

fun day21Part1(lines: List<String>): Any {
    val (p1, p2) = lines.map { Player(it.last().digitToInt()) }
    val die = DeterministicDie()
    while (true) {
        p1.move(die)
        if (p1.score >= 1000) break
        p2.move(die)
        if (p2.score >= 1000) break
    }
    return minOf(p1.score, p2.score) * die.counter
}

fun day21Part2(lines: List<String>): Any {
    val (p1Pos, p2Pos) = lines.map { it.last().digitToInt() }
    return numberOfWins(p1Pos, p2Pos, 0, 0, true)
}

private val possibleMoves = arrayOf(3, 4, 5, 4, 5, 6, 5, 6, 7, 4, 5, 6, 5, 6, 7, 6, 7, 8, 5, 6, 7, 6, 7, 8, 7, 8, 9)

private val numberOfWins = ::numberOfWins0.memoize()

private fun numberOfWins0(pos1: Int, pos2: Int, score1: Int, score2: Int, p1Turn: Boolean): Pair<BigInteger, BigInteger> {
    if (score1 >= 21) {
        return BigInteger.ONE to BigInteger.ZERO
    }
    if (score2 >= 21) {
        return BigInteger.ZERO to BigInteger.ONE
    }

    if (p1Turn) {
        val newPositions = possibleMoves.map { (pos1 + it - 1) % 10 + 1 }
        return newPositions.map {
            numberOfWins(it, pos2, score1 + it, score2, !p1Turn)
        }.reduce { acc, pair -> acc + pair }
    } else {
        val newPositions = possibleMoves.map { (pos2 + it - 1) % 10 + 1 }
        return newPositions.map {
            numberOfWins(pos1, it, score1, score2 + it, !p1Turn)
        }.reduce { acc, pair -> acc + pair }
    }
}

operator fun Pair<BigInteger, BigInteger>.plus(other: Pair<BigInteger, BigInteger>) = first + other.first to second + other.second