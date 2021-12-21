package day5

import util.getInput

fun main() {
    println(
        day5Part1(
            """
0,9 -> 5,9
8,0 -> 0,8
9,4 -> 3,4
2,2 -> 2,1
7,0 -> 7,4
6,4 -> 2,0
0,9 -> 2,9
3,4 -> 1,4
0,0 -> 8,8
5,5 -> 8,2
    """.trimIndent().split('\n')
        )
    )
    println(day5Part1(getInput(5)))
    println(
        day5Part2(
            """
0,9 -> 5,9
8,0 -> 0,8
9,4 -> 3,4
2,2 -> 2,1
7,0 -> 7,4
6,4 -> 2,0
0,9 -> 2,9
3,4 -> 1,4
0,0 -> 8,8
5,5 -> 8,2
    """.trimIndent().split('\n')
        )
    )
    println(day5Part2(getInput(5)))
}

data class Point(val x: Int, val y: Int)

fun day5Part1(input: List<String>): Any {
    val segments = parseInput(input).filter { it.xRange.first == it.xRange.last || it.yRange.first == it.yRange.last }.map {
        val (startX, startY) = it.xRange.first to it.yRange.first
        val (endX, endY) = it.xRange.last to it.yRange.last
        LineSegment(min(startX, endX)..max(startX, endX), min(startY, endY)..max(startY, endY))
    }
    val coveredPoints = mutableSetOf<Point>()
    val pointsCoveredTwice = mutableSetOf<Point>()
    for (segment in segments) {
        for (x in segment.xRange) {
            for (y in segment.yRange) {
                val point = Point(x, y)
                if (point in coveredPoints) {
                    pointsCoveredTwice.add(point)
                } else {
                    coveredPoints.add(point)
                }
            }
        }
    }
    return pointsCoveredTwice.size
}

fun day5Part2(input: List<String>): Any {
    val segments = parseInput(input)
    val coveredPoints = mutableSetOf<Point>()
    val pointsCoveredTwice = mutableSetOf<Point>()
    for (segment in segments) {
        for (point in segment.iterator()) {
            if (point in coveredPoints) {
                pointsCoveredTwice.add(point)
            } else {
                coveredPoints.add(point)
            }
        }
    }
    return pointsCoveredTwice.size
}

private data class LineSegment(val xRange: IntRange, val yRange: IntRange) {
    operator fun iterator() = when {
        xRange.first == xRange.last || yRange.first == yRange.last -> sequence {
            val (startX, startY) = xRange.first to yRange.first
            val (endX, endY) = xRange.last to yRange.last
            for (x in min(startX, endX)..max(startX, endX)) {
                for (y in min(startY, endY)..max(startY, endY)) {
                    yield(Point(x, y))
                }
            }
        }
        else -> sequence {
            val xStep = if (xRange.first > xRange.last) -1 else 1
            val yStep = if (yRange.first > yRange.last) -1 else 1
            var x = xRange.first
            var y = yRange.first
            val (startX, startY) = xRange.first to yRange.first
            val (endX, endY) = xRange.last to yRange.last
            val xBounds = min(startX, endX)..max(startX, endX)
            val yBounds = min(startY, endY)..max(startY, endY)
            while (x in xBounds && y in yBounds) {
                yield(Point(x, y))
                x += xStep
                y += yStep
            }
        }
    }
}

private fun parseInput(input: List<String>): List<LineSegment> {
    return input.map { line ->
        line.split(" -> ").map { point ->
            point.split(',').let { (x, y) ->
                x.toInt() to y.toInt()
            }
        }.let { (left, right) -> left to right }
    }.map { LineSegment(it.first.first..it.second.first, it.first.second..it.second.second) }
}

private fun min(a: Int, b: Int) = if (a > b) b else a

private fun max(a: Int, b: Int) = if (a < b) b else a