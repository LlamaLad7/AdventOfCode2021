package day20

import util.getInput

fun main() {
    val testInput = """
        ..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#

        #..#.
        #....
        ##..#
        ..#..
        ..###
    """.trimIndent().split('\n')
    println(day20Part1(testInput))
    println(day20Part1(getInput(20)))
    println(day20Part2(testInput))
    println(day20Part2(getInput(20)))
}

private typealias Image = Array<BooleanArray>

val Image.width get() = this[0].size
val Image.height get() = this.size
fun Image.print() = println(joinToString("\n") { line -> line.joinToString("") { if (it) "#" else "." } })

fun day20Part1(lines: List<String>): Any {
    val (algorithm, image) = parseInput(lines)
    return image.enhance(algorithm, 2).sumOf { line -> line.count { it } }
}

fun day20Part2(lines: List<String>): Any {
    val (algorithm, image) = parseInput(lines)
    return image.enhance(algorithm, 50).sumOf { line -> line.count { it } }
}

private fun Image.enhance(algorithm: String, times: Int): Image {
    var oldImage = this
    var theVoidValue = false // The infinite expanse of pixels begins as all .s
    repeat(times) {
        val newImage: Image = Array(oldImage.height + 2) { BooleanArray(oldImage.width + 2) }
        for ((rowIndex, row) in newImage.withIndex()) {
            val y = rowIndex - 1
            for (pixelIndex in row.indices) {
                val x = pixelIndex - 1
                val indicesToCheck = listOf(
                    y - 1 to x - 1,
                    y - 1 to x,
                    y - 1 to x + 1,
                    y to x - 1,
                    y to x,
                    y to x + 1,
                    y + 1 to x - 1,
                    y + 1 to x,
                    y + 1 to x + 1
                )
                val oldPixelValues = indicesToCheck.map { (y, x) ->
                    oldImage.getOrNull(y)?.getOrNull(x) ?: theVoidValue
                }
                val binaryNumber = oldPixelValues.joinToString("") { if (it) "1" else "0" }.toInt(2)
                newImage[rowIndex][pixelIndex] = algorithm[binaryNumber] == '#'
            }
        }
        oldImage = newImage
        // Make sure to account for the infinite expanse of pixels possibly being flipped
        theVoidValue = if (theVoidValue) algorithm.last() == '#' else algorithm.first() == '#'
    }
    return oldImage
}

private fun parseInput(lines: List<String>): Pair<String, Image> {
    return lines[0] to lines.drop(2).map { line -> line.map { it == '#' }.toBooleanArray() }.toTypedArray()
}