package day6

import com.aballano.mnemonik.memoize
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.sum
import util.getRawInput
import java.math.BigInteger

fun main() {
    println(day6Part1("""3,4,3,1,2"""))
    println(day6Part1(getRawInput(6)))
    println(day6Part2("""3,4,3,1,2"""))
    println(day6Part2(getRawInput(6)))
}

fun day6Part1(numbers: String): Any {
    val fish = numbers.trimEnd().split(',').map { it.toInt() }.toMutableList()
    repeat(80) {
        val indices = fish.indices
        for (i in indices) {
            fish[i] -= 1
            if (fish[i] == -1) {
                fish[i] = 6
                fish.add(8)
            }
        }
    }
    return fish.size
}

fun day6Part2(numbers: String): Any {
    val fish = numbers.trimEnd().split(',').map { it.toInt() }
    val arr = Array(9) { BigInteger.ZERO }
    for (num in fish) {
        arr[num]++
    }
    repeat(256) {
        val zeros = arr[0]
        arr[0] = arr[1]
        arr[1] = arr[2]
        arr[2] = arr[3]
        arr[3] = arr[4]
        arr[4] = arr[5]
        arr[5] = arr[6]
        arr[6] = arr[7]
        arr[7] = arr[8]
        arr[8] = zeros
        arr[6] += zeros
    }
    return arr.sumOf { it }
}

fun day6Part2Recursive(numbers: String): Any {
    val startingFish = numbers.trimEnd().split(',').map { it.toInt() }
    var total = 0L
    for (timer in startingFish) {
        total++
        total += memoizedFish(0, timer)
    }
    return total
}

val memoizedFish = ::fishMadeByThisFish.memoize()

private fun fishMadeByThisFish(spawningDay: Int, startingTimer: Int): Long {
    var day = spawningDay + startingTimer + 1
    var newFish = 0L
    if (day > 256) return 0
    while (day <= 256) {
        newFish++
        newFish += memoizedFish(day, 8)
        day += 7
    }

    return newFish
}

fun day6Part2LinAlg(numbers: String): Long {
    val startingFish = numbers.trimEnd().split(',').map { it.toInt() }
    val frequencies = mk.zeros<Long>(9)
    for (num in startingFish) {
        frequencies[num]++
    }
    val mat = mk.ndarray<Long>(mk[
            mk[0, 1, 0, 0, 0, 0, 0, 0, 0],
            mk[0, 0, 1, 0, 0, 0, 0, 0, 0],
            mk[0, 0, 0, 1, 0, 0, 0, 0, 0],
            mk[0, 0, 0, 0, 1, 0, 0, 0, 0],
            mk[0, 0, 0, 0, 0, 1, 0, 0, 0],
            mk[0, 0, 0, 0, 0, 0, 1, 0, 0],
            mk[1, 0, 0, 0, 0, 0, 0, 1, 0],
            mk[0, 0, 0, 0, 0, 0, 0, 0, 1],
            mk[1, 0, 0, 0, 0, 0, 0, 0, 0],
    ])

    return (mk.linalg.pow(mat, 256) dot frequencies).sum()
}