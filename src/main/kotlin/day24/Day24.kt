package day24

import com.aballano.mnemonik.memoize
import util.getInput
import kotlin.math.pow

private val instructions = getInput(24).map { it.split(' ') }

private lateinit var dfs: (Int, Int, Int, Int, Int) -> Pair<Boolean, String>

private lateinit var digitsToTry: IntProgression

fun main() {
    println(day24Part1())
    println(day24Part2())
}

fun day24Part1(): Any {
    digitsToTry = 9 downTo 1
    dfs = ::dfs0.memoize()
    return dfs(0, 0, 0, 0, 0).second
}

fun day24Part2(): Any {
    digitsToTry = 1..9
    dfs = ::dfs0.memoize()
    return dfs(0, 0, 0, 0, 0).second
}

// Returns whether it worked and the data if so
private fun dfs0(insnIndex: Int, w: Int, x: Int, y: Int, z: Int): Pair<Boolean, String> {
    val values = mutableMapOf('w' to w, 'x' to x, 'y' to y, 'z' to z)
    if (insnIndex >= instructions.size) {
        // At the end of the instructions
        return (z == 0) to ""
    }
    if (z > 10f.pow(7)) {
        // Probably not solvable
        return false to ""
    }

    val insn = instructions[insnIndex]
    val targetVar = insn[1].first()

    fun evaluate(operand: String) = if (operand in "wxyz") values[operand.first()]!! else operand.toInt()

    when (insn[0]) {
        "inp" -> {
            for (possibleDigit in digitsToTry) {
                values[targetVar] = possibleDigit
                val (worked, digits) = dfs(insnIndex + 1, values['w']!!, values['x']!!, values['y']!!, values['z']!!)
                if (worked) {
                    return true to "$possibleDigit$digits"
                }
            }
            return false to ""
        }
        "add" -> values[targetVar] = values.getValue(targetVar) + evaluate(insn[2])
        "mul" -> values[targetVar] = values.getValue(targetVar) * evaluate(insn[2])
        "div" -> values[targetVar] = values.getValue(targetVar) / evaluate(insn[2])
        "mod" -> values[targetVar] = values.getValue(targetVar) % evaluate(insn[2])
        "eql" -> values[targetVar] = if (values.getValue(targetVar) == evaluate(insn[2])) 1 else 0
        else -> error("Unsupported operation ${insn[0]}")
    }
    return dfs(insnIndex + 1, values['w']!!, values['x']!!, values['y']!!, values['z']!!)
}