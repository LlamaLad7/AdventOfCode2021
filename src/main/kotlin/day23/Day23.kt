package day23

import util.getInput
import kotlin.math.abs
import java.util.*
import kotlin.properties.Delegates

fun main() {
    val testInput = """
        #############
        #...........#
        ###B#C#B#D###
          #A#D#C#A#
          #########
    """.trimIndent().split('\n')
    day23part1(testInput)
    day23part1(getInput(23))
    day23part2(testInput)
    day23part2(getInput(23))
}

private fun day23part1(lines: List<String>) {
    ROOM_SIZE = 2
    dijkstra(lines)
}

private fun day23part2(lines: List<String>) {
    ROOM_SIZE = 4
    val newLines = lines.toMutableList()
    newLines.add(3, "  #D#C#B#A#")
    newLines.add(4, "  #D#B#A#C#")
    dijkstra(newLines)
}

private fun dijkstra(lines: List<String>) {
    val start = readInput(lines)
    val q = PriorityQueue<WeightedState>()
    q.add(WeightedState(0, start))
    val dist = mutableMapOf(start to 0)

    while (q.isNotEmpty()) {
        val (weight, state) = q.remove()
        if (weight != dist[state]) continue
        if (state.isSolved()) {
            println(weight)
            return
        }
        for ((newWeight, newState) in state.getConnectedStates(weight)) {
            if (newWeight < dist.getOrPut(newState) { Int.MAX_VALUE }) {
                dist[newState] = newWeight
                q.add(WeightedState(newWeight, newState))
            }
        }
    }
}

private data class WeightedState(val weight: Int, val state: State) : Comparable<WeightedState> {
    override fun compareTo(other: WeightedState) = weight.compareTo(other.weight)
}

private var ROOM_SIZE by Delegates.notNull<Int>()

private data class State(private val hallway: List<Char>, private val rooms: List<List<Char>>) {
    fun getConnectedStates(currentWeight: Int) = sequence {
        for ((start, c) in hallway.withIndex()) {
            if (c == '.') continue
            val desiredRoom = c - 'A'
            if (rooms[desiredRoom].any { it != c }) continue
            if (makeRange(start, roomToHallway(desiredRoom)).drop(1).all { hallway[it] == '.' }) {
                val spacesMoved = abs(roomToHallway(desiredRoom) - start) + (ROOM_SIZE - rooms[desiredRoom].size + 1)
                yield(WeightedState(currentWeight + c.toCost() * spacesMoved, moveFromHallwayToRoom(start)))
            }
        }
        for ((start, room) in rooms.withIndex()) {
            val c = room.firstOrNull() ?: continue
            if (start == c - 'A' && room.all { it == c }) continue
            for (end in validHallwaySpaces) {
                if (makeRange(roomToHallway(start), end).all { hallway[it] == '.' }) {
                    val spacesMoved = abs(end - roomToHallway(start)) + (ROOM_SIZE - room.size)
                    yield(WeightedState(currentWeight + c.toCost() * spacesMoved, moveFromRoomToHallway(start, end)))
                }
            }
        }
    }

    fun isSolved() = hallway.all { it == '.' } && rooms.withIndex().all { (index, room) -> room.all { it == 'A' + index } }

    private fun moveFromHallwayToRoom(pos: Int): State {
        val current = hallway[pos]
        val room = current - 'A'
        val hallway = hallway.toMutableList()
        val rooms = rooms.map { it.toMutableList() }
        rooms[room].add(0, current)
        hallway[pos] = '.'
        return State(hallway, rooms)
    }

    private fun moveFromRoomToHallway(room: Int, pos: Int): State {
        val hallway = hallway.toMutableList()
        val rooms = rooms.map { it.toMutableList() }
        val current = rooms[room].removeFirst()
        hallway[pos] = current
        return State(hallway, rooms)
    }

    private fun roomToHallway(room: Int) = room * 2 + 2

    private fun isSpaceOutsideRoom(space: Int) = when (space) {
        2, 4, 6, 8 -> true
        else -> false
    }

    private companion object {
        private val validHallwaySpaces = intArrayOf(0, 1, 3, 5, 7, 9, 10)
    }
}

private fun Char.toCost() = when (this) {
    'A' -> 1
    'B' -> 10
    'C' -> 100
    'D' -> 1000
    else -> error("Invalid char $this")
}

private fun makeRange(start: Int, end: Int) = if (end < start) start downTo end else start..end

private fun readInput(lines: List<String>): State {
    val hallway = List(11) { '.' }
    val rooms = List(4) { index -> (2..lines.size - 2).map { lines[it][index * 2 + 3] } }
    return State(hallway, rooms)
}