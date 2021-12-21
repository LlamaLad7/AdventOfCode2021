package day13

import util.getInput

fun main() {
    val testInput = """
        6,10
        0,14
        9,10
        0,3
        10,4
        4,11
        6,0
        6,12
        4,1
        0,13
        10,12
        3,4
        3,0
        8,4
        1,10
        2,14
        8,10
        9,0

        fold along y=7
        fold along x=5
    """.trimIndent().split('\n')
    println(day13Part1(testInput))
    println(day13Part1(getInput(13)))
    println(day13Part2(testInput))
    println(day13Part2(getInput(13)))
}

fun day13Part1(lines: List<String>): Any {
    val (dots, folds) = parseInput(lines)
    println("${dots.matrix.size}x${dots.matrix[0].size}")
    dots.fold(folds[0])
    return dots.matrix.sumOf { arr -> arr.count { it } }
}

fun day13Part2(lines: List<String>): Any {
    val (dots, folds) = parseInput(lines)
    for (fold in folds) {
        dots.fold(fold)
    }
    dots.matrix.print()
    return "read what's above ^"
}

private class Dots(lines: List<String>) {
    var matrix = run {
        val points = lines.map { line -> line.split(',').let { (x, y) -> Point(x.toInt(), y.toInt()) } }
        val maxX = points.maxOf { it.x }
        val maxY = points.maxOf { it.y }
        val arr = Array(maxY + 1) { BooleanArray(maxX + 1) }
        for ((x, y) in points) {
            arr[y][x] = true
        }
        return@run arr
    }

    fun fold(fold: Fold) {
        when (fold.type) {
            FoldType.LEFT -> {
                require(fold.position * 2 == matrix[0].size - 1)
                val rightHalf =
                    matrix.map { it.copyOfRange(fold.position + 1, it.size).reversedArray() }.toTypedArray()

                matrix = matrix.map { it.copyOfRange(0, fold.position) }.toTypedArray()

                matrix.overlay(rightHalf)
            }
            FoldType.UP -> {
                require(fold.position * 2 == matrix.size - 1)
                val bottomHalf =
                    matrix.copyOfRange(fold.position + 1, matrix.size).map { it.copyOf() }.asReversed().toTypedArray()

                matrix = matrix.copyOfRange(0, fold.position)

                matrix.overlay(bottomHalf)
            }
        }
    }
}

private fun Array<BooleanArray>.print() =
    println(this.joinToString("\n") { row -> row.map { if (it) '#' else '.' }.joinToString("") })

private data class Fold(val type: FoldType, val position: Int)

private enum class FoldType {
    LEFT, UP
}

private data class Point(val x: Int, val y: Int)

private fun parseInput(lines: List<String>): Pair<Dots, List<Fold>> {
    val index = lines.indexOf("")
    val coordinates = lines.subList(0, index)
    val folds = lines.subList(index + 1, lines.size)
        .map {
            it.split('=').let { (first, second) -> Fold(first.last().toFoldType(), second.toInt()) }
        }
    return Dots(coordinates) to folds
}

private fun Char.toFoldType() = when (this) {
    'x' -> FoldType.LEFT
    'y' -> FoldType.UP
    else -> error("Invalid fold axis $this")
}

private fun Array<BooleanArray>.overlay(other: Array<BooleanArray>) {
    for ((rowIndex, row) in other.withIndex()) {
        this[rowIndex] = this[rowIndex].zip(row).map { (a, b) -> a || b }.toBooleanArray()
    }
}