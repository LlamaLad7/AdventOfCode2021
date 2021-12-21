package day17

import util.getRawInput

fun main() {
    val testInput = """
        target area: x=20..30, y=-10..-5
    """.trimIndent()
    println(day17Part1(testInput))
    println(day17Part1(getRawInput(17)))
    println(day17Part2(testInput))
    println(day17Part2(getRawInput(17)))
}

fun day17Part1(areaDescriptor: String): Any {
    val area = getArea(areaDescriptor)
    return bruteForcePart1(area)
}

private fun bruteForcePart1(area: Area): Int {
    var maxY = Int.MIN_VALUE
    for (startingXVelocity in 0..area.xRange.last) {
        for (startingYVelocity in -10000..10000) {
            val probe = Probe(xVelocity = startingXVelocity, yVelocity = startingYVelocity)
            var maxYOnThisGo = Int.MIN_VALUE
            while (true) {
                maxYOnThisGo = maxOf(maxYOnThisGo, probe.y)
                probe.step()
                if (probe.hasPassed(area) || (probe.hasStopped() && probe.x !in area.xRange)) {
                    break
                }
                if (probe.isIn(area)) {
                    maxY = maxOf(maxY, maxYOnThisGo)
                    break
                }

            }
        }
    }
    return maxY
}

fun day17Part2(areaDescriptor: String): Any {
    val area = getArea(areaDescriptor)
    return bruteForcePart2(area)
}

private fun bruteForcePart2(area: Area): Int {
    var numberOfStartingVelocities = 0
    for (startingXVelocity in 0..area.xRange.last) {
        for (startingYVelocity in -10000..10000) {
            val probe = Probe(xVelocity = startingXVelocity, yVelocity = startingYVelocity)
            while (true) {
                probe.step()
                if (probe.hasPassed(area) || (probe.hasStopped() && probe.x !in area.xRange)) {
                    break
                }
                if (probe.isIn(area)) {
                    numberOfStartingVelocities++
                    break
                }

            }
        }
    }
    return numberOfStartingVelocities
}

private fun getArea(descriptor: String): Area {
    return descriptor
        .replace("target area: x=", "")
        .replace("y=", "")
        .split(", ")
        .map { it.split("..").let { (a, b) -> a.toInt()..b.toInt() } }
        .let { (a, b) -> Area(a, b) }
}

private data class Area(val xRange: IntRange, val yRange: IntRange)

private data class Probe(var x: Int = 0, var y: Int = 0, var xVelocity: Int, var yVelocity: Int) {
    fun step() {
        x += xVelocity
        y += yVelocity
        xVelocity += when {
            xVelocity > 0 -> -1
            xVelocity < 0 -> 1
            else -> 0
        }
        yVelocity--
    }

    fun isIn(area: Area) = x in area.xRange && y in area.yRange

    fun hasPassed(area: Area) = x > area.xRange.last || y < area.yRange.first

    fun hasStopped() = xVelocity == 0
}