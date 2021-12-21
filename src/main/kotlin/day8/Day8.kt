package day8

import util.getInput
import java.io.File

fun main() {
    val testInput = """
        be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
        edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
        fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
        fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
        aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
        fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
        dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
        bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
        egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
        gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce
    """.trimIndent().split('\n')
    println(day8Part1(testInput))
    println(day8Part1(getInput(8)))
    println(day8Part2(testInput))
    println(day8Part2(getInput(8)))
}

private val digitMappingsPart1 = mutableMapOf(
    2 to 1, 4 to 4, 3 to 7, 7 to 8
)

fun day8Part1(lines: List<String>): Any {
    val output = lines.map { it.split(" | ")[1].split(' ') }
    return output.sumOf { it.count { str -> str.length in digitMappingsPart1 } }
}

fun day8Part2(lines: List<String>): Any {
    val lines = lines.map { line -> line.split(" | ").map { it.split(' ') } }
    val perms = File("src/main/resources/day8helper.txt").readLines()
    var total = 0

    for ((start, end) in lines) {
        lateinit var correctPerm: Map<Char, Segment>
        perms@ for (perm in perms) {
            val mapping = mutableMapOf<Char, Segment>()
            for ((segment, char) in perm.withIndex().map { (index, value) -> Segment.values()[index] to value }) {
                mapping[char] = segment
            }

            for (word in start) {
                val segments = mapping.entries.filter { it.key in word }.map { it.value }.toSet()
                segmentMapping.values.find { it == segments } ?: continue@perms
            }
            correctPerm = mapping
            break@perms
        }


        total += buildString {
            for (word in end) {
                val segments = word.map { correctPerm[it]!! }.toSet()
                append(segmentMapping.entries.find { it.value == segments }!!.key)
            }
        }.toInt()
    }

    return total
}

fun day8Part2Better(lines: List<String>): Any {
    val lines = lines.map { line -> line.split(" | ").map { it.split(' ') } }
    var total = 0
    for ((input, output) in lines) {
        val mapping = mutableMapOf<Set<Char>, Int>()
        for (str in input) {
            mapping[str.toSet()] = -1
        }
        mapping.keys.filter { it.size in digitMappingsPart1 }.forEach { mapping[it] = digitMappingsPart1[it.size]!! }
        for (set in mapping.keys.filter { it.size == 6 }) {
            when {
                !set.containsAll(mapping.getKey(1)) -> mapping[set] = 6
                set.containsAll(mapping.getKey(4)) -> mapping[set] = 9
                else -> mapping[set] = 0
            }
        }

        for (set in mapping.keys.filter { it.size == 5 }) {
            when {
                set.containsAll(mapping.getKey(1)) -> mapping[set] = 3
                mapping.getKey(6).containsAll(set) -> mapping[set] = 5
                else -> mapping[set] = 2
            }
        }

        total += buildString {
            for (digit in output) {
                append(mapping[digit.toSet()])
            }
        }.toInt()
    }
    return total
}

private fun <K, V> Map<K, V>.getKey(value: V) = entries.find { it.value == value }!!.key

private enum class Segment {
    TOP, TOP_RIGHT, TOP_LEFT, MIDDLE, BOTTOM_RIGHT, BOTTOM_LEFT, BOTTOM
}

private val segmentMapping = mapOf(
    0 to Segment.values().toSet() - setOf(Segment.MIDDLE),
    1 to setOf(Segment.TOP_RIGHT, Segment.BOTTOM_RIGHT),
    2 to setOf(Segment.TOP, Segment.TOP_RIGHT, Segment.MIDDLE, Segment.BOTTOM_LEFT, Segment.BOTTOM),
    3 to setOf(Segment.TOP, Segment.TOP_RIGHT, Segment.MIDDLE, Segment.BOTTOM_RIGHT, Segment.BOTTOM),
    4 to setOf(Segment.TOP_RIGHT, Segment.TOP_LEFT, Segment.MIDDLE, Segment.BOTTOM_RIGHT),
    5 to setOf(Segment.TOP, Segment.TOP_LEFT, Segment.MIDDLE, Segment.BOTTOM_RIGHT, Segment.BOTTOM),
    6 to Segment.values().toSet() - setOf(Segment.TOP_RIGHT),
    7 to setOf(Segment.TOP, Segment.TOP_RIGHT, Segment.BOTTOM_RIGHT),
    8 to Segment.values().toSet(),
    9 to Segment.values().toSet() - setOf(Segment.BOTTOM_LEFT)
)