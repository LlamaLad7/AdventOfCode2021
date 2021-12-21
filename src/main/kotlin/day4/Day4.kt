package day4

import util.getRawInput

fun main() {
    println(
        day4Part1(
            """
7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

22 13 17 11  0
 8  2 23  4 24
21  9 14 16  7
 6 10  3 18  5
 1 12 20 15 19

 3 15  0  2 22
 9 18 13 17  5
19  8  7 25 23
20 11 10 24  4
14 21 16 12  6

14 21 17 24  4
10 16 15  9 19
18  8 23 26 20
22 11 13  6  5
 2  0 12  3  7
    """.trimIndent()
        )
    )
    println(day4Part1(getRawInput(4)))
    println(
        day4Part2(
            """
7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

22 13 17 11  0
 8  2 23  4 24
21  9 14 16  7
 6 10  3 18  5
 1 12 20 15 19

 3 15  0  2 22
 9 18 13 17  5
19  8  7 25 23
20 11 10 24  4
14 21 16 12  6

14 21 17 24  4
10 16 15  9 19
18  8 23 26 20
22 11 13  6  5
 2  0 12  3  7
    """.trimIndent()
        )
    )
    println(day4Part2(getRawInput(4)))
}

private class Cell(val num: Int) {
    var marked = false
}

private class Board(boardText: String) {
    private val nums = boardText.split('\n').filterNot { it.isEmpty() }
        .map { row -> row.trim().replace("  ", " ").split(' ').map { cell -> Cell(cell.toInt()) } }

    fun mark(num: Int) {
        for (row in nums) {
            for (cell in row) {
                if (cell.num == num) {
                    cell.marked = true
                }
            }
        }
    }

    fun hasWon(): Boolean {
        for (i in nums.indices) {
            if (nums[i].all { it.marked } || nums.all { it[i].marked }) {
                return true
            }
        }
        return false
    }

    fun sumOfUnmarkedNumbers(): Int {
        return nums.sumOf { row -> row.sumOf { if (it.marked) 0 else it.num } }
    }
}

fun day4Part1(input: String): Any {
    val (nums, boards) = parseInput(input)
    for (num in nums) {
        for (board in boards) {
            board.mark(num)
            if (board.hasWon()) {
                return board.sumOfUnmarkedNumbers() * num
            }
        }
    }
    return -1
}

fun day4Part2(input: String): Any {
    val (nums, boards) = parseInput(input).let { it.first to it.second.toMutableList() }
    for (num in nums) {
        val iterator = boards.iterator()
        while (iterator.hasNext()) {
            val board = iterator.next()
            board.mark(num)
            if (board.hasWon()) {
                iterator.remove()
                if (boards.isEmpty()) {
                    return board.sumOfUnmarkedNumbers() * num
                }
            }
        }
    }
    return -1
}

private fun parseInput(input: String): Pair<List<Int>, List<Board>> {
    val pieces = input.split("\n\n").toMutableList()
    val nums = pieces.removeAt(0).split(',').map { it.toInt() }
    val boards = pieces.map { Board(it) }
    return nums to boards
}