package day7

import util.getInput
import java.math.BigInteger
import kotlin.math.abs

fun main() {
    println(
        day7Part1(
            """
16,1,2,0,4,2,7,1,2,14
    """.trimIndent().split('\n')
        )
    )
    println(day7Part1(getInput(7)))
    println(
        day7Part2(
            """
16,1,2,0,4,2,7,1,2,14
    """.trimIndent().split('\n')
        )
    )
    println(day7Part2
        (getInput(7)))
}

fun day7Part1(lines: List<String>): Any {
    val nums = lines[0].split(',').map { it.toInt() }
    var minFuel = Int.MAX_VALUE
    for (i in 0..nums.maxOf { it }) {
        val totalDiff = nums.sumOf { abs(it - i) }
        if (totalDiff < minFuel) {
            minFuel = totalDiff
        }
    }
    return minFuel
}

fun day7Part1Median(lines: List<String>): Any {
    val nums = lines[0].split(',').map { it.toInt() }
    val median = nums.sorted()[nums.size / 2]
    return nums.sumOf { abs(it - median) }
}

fun day7Part2(lines: List<String>): Any {
    val nums = lines[0].split(',').map { it.toInt() }
    var minFuel: BigInteger? = null
    for (i in 0..nums.maxOf { it }) {
        val totalDiff = nums.sumOf { getFuel(abs(it - i)) }
        if (minFuel == null || totalDiff < minFuel) {
            minFuel = totalDiff
        }
    }
    return minFuel!!
}

private fun getFuel(dist: Int): BigInteger {
    return (dist.toBigInteger() * (BigInteger.ONE + dist.toBigInteger())) / BigInteger.TWO
}

fun day7Part2Mean(lines: List<String>): Any {
    val nums = lines[0].split(',').map { it.toInt() }
    val mean = nums.average().toInt()
    return nums.sumOf { getFuel(abs(it - mean)) }
}