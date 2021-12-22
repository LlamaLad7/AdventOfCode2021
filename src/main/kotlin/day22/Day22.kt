package day22

import util.getInput
import java.math.BigInteger

fun main() {
    val testInput = """
        on x=-20..26,y=-36..17,z=-47..7
        on x=-20..33,y=-21..23,z=-26..28
        on x=-22..28,y=-29..23,z=-38..16
        on x=-46..7,y=-6..46,z=-50..-1
        on x=-49..1,y=-3..46,z=-24..28
        on x=2..47,y=-22..22,z=-23..27
        on x=-27..23,y=-28..26,z=-21..29
        on x=-39..5,y=-6..47,z=-3..44
        on x=-30..21,y=-8..43,z=-13..34
        on x=-22..26,y=-27..20,z=-29..19
        off x=-48..-32,y=26..41,z=-47..-37
        on x=-12..35,y=6..50,z=-50..-2
        off x=-48..-32,y=-32..-16,z=-15..-5
        on x=-18..26,y=-33..15,z=-7..46
        off x=-40..-22,y=-38..-28,z=23..41
        on x=-16..35,y=-41..10,z=-47..6
        off x=-32..-23,y=11..30,z=-14..3
        on x=-49..-5,y=-3..45,z=-29..18
        off x=18..30,y=-20..-8,z=-3..13
        on x=-41..9,y=-7..43,z=-33..15
        on x=-54112..-39298,y=-85059..-49293,z=-27449..7877
        on x=967..23432,y=45373..81175,z=27513..53682
    """.trimIndent().split('\n')
    println(day22Part1(testInput))
    println(day22Part1(getInput(22)))
    println(day22Part2(testInput))
    println(day22Part2(getInput(22)))
}

fun day22Part1(lines: List<String>): Any {
    val steps = parseInput(lines).toMutableList()
    steps.removeIf { (cuboid, _) -> arrayOf(cuboid.x, cuboid.y, cuboid.z).any { it.first < -50 || it.last > 50 } }
    val grid = Grid()
    for ((cuboid, on) in steps) {
        if (on) {
            grid.turnOn(cuboid)
        } else {
            grid.turnOff(cuboid)
        }
    }
    return grid.onCount()
}

fun day22Part2(lines: List<String>): Any {
    val steps = parseInput(lines).toMutableList()
    val grid = EfficientGrid()
    for ((cuboid, on) in steps) {
        if (on) {
            grid.turnOn(cuboid)
        } else {
            grid.turnOff(cuboid)
        }
    }
    return grid.onCount()
}

private fun parseInput(lines: List<String>): List<Pair<Cuboid, Boolean>> {
    val result = mutableListOf<Pair<Cuboid, Boolean>>()
    for (line in lines) {
        val (start, rest) = line.split(' ')
        val on = start == "on"
        val (x, y, z) = rest.split(',').map { part ->
            part.substringAfter('=').split("..").let { (start, end) -> start.toInt()..end.toInt() }
        }
        result.add(Cuboid(x, y, z) to on)
    }
    return result
}

private data class Point(val x: Int, val y: Int, val z: Int)

private class Grid {
    private val points = mutableSetOf<Point>()

    fun turnOn(cuboid: Cuboid) {
        for (x in cuboid.x) {
            for (y in cuboid.y) {
                for (z in cuboid.z) {
                    points.add(Point(x, y, z))
                }
            }
        }
    }

    fun turnOff(cuboid: Cuboid) {
        points.removeIf { (x, y, z) -> x in cuboid.x && y in cuboid.y && z in cuboid.z }
    }

    fun onCount() = points.size
}

private class EfficientGrid {
    private var onCuboids = mutableListOf<Cuboid>()

    fun turnOn(cuboid: Cuboid) {
        onCuboids.add(cuboid)
    }

    fun turnOff(offCuboid: Cuboid) {
        onCuboids = onCuboids.flatMap { onCuboid ->
            if (!onCuboid.overlapsWith(offCuboid)) {
                listOf(onCuboid)
            } else {
                val intersection = onCuboid.intersection(offCuboid)
                onCuboid.splitByRemoving(intersection)
            }
        }.toMutableList()
    }

    fun onCount(): BigInteger {
        var reduced = false
        while (!reduced) {
            reduced = true
            onCuboids = onCuboids.flatMapIndexed { thisCuboidIndex, thisCuboid ->
                for (otherCuboid in onCuboids.drop(thisCuboidIndex + 1)) {
                    if (thisCuboid.overlapsWith(otherCuboid)) {
                        reduced = false
                        val intersection = thisCuboid.intersection(otherCuboid)
                        return@flatMapIndexed thisCuboid.splitByRemoving(intersection)
                    }
                }
                listOf(thisCuboid)
            }.toMutableList()
        }
        return onCuboids.sumOf { it.volume() }
    }
}

private data class Cuboid(val x: IntRange, val y: IntRange, val z: IntRange) {
    fun intersection(other: Cuboid) = Cuboid(
        maxOf(x.first, other.x.first)..minOf(x.last, other.x.last),
        maxOf(y.first, other.y.first)..minOf(y.last, other.y.last),
        maxOf(z.first, other.z.first)..minOf(z.last, other.z.last),
    )

    // Other must be entirely contained within this!
    fun splitByRemoving(other: Cuboid) = buildList {
        val thisCuboid = this@Cuboid
        add(Cuboid(thisCuboid.x.first until other.x.first, thisCuboid.y, thisCuboid.z))
        add(Cuboid((other.x.last + 1)..thisCuboid.x.last, thisCuboid.y, thisCuboid.z))
        add(Cuboid(other.x, thisCuboid.y.first until other.y.first, thisCuboid.z))
        add(Cuboid(other.x, (other.y.last + 1)..thisCuboid.y.last, thisCuboid.z))
        add(Cuboid(other.x, other.y, thisCuboid.z.first until other.z.first))
        add(Cuboid(other.x, other.y, (other.z.last + 1)..thisCuboid.z.last))
    }.filterNot { it.x.isEmpty() || it.y.isEmpty() || it.z.isEmpty() }

    fun overlapsWith(other: Cuboid) = !(
            x.first > other.x.last ||
                    x.last < other.x.first ||
                    y.first > other.y.last ||
                    y.last < other.y.first ||
                    z.first > other.z.last ||
                    z.last < other.z.first
            )

    fun volume() = x.size.toBigInteger() * y.size.toBigInteger() * z.size.toBigInteger()
}

// Returns whether this completely contains the other
operator fun IntRange.contains(other: IntRange) = other.first in this && other.last in this

val IntRange.size get() = last - first + 1