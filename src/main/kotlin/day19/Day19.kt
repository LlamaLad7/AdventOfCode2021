package day19

import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.joinToString
import util.getInput
import kotlin.math.abs

fun main() {
    val testInput = """
        --- scanner 0 ---
        404,-588,-901
        528,-643,409
        -838,591,734
        390,-675,-793
        -537,-823,-458
        -485,-357,347
        -345,-311,381
        -661,-816,-575
        -876,649,763
        -618,-824,-621
        553,345,-567
        474,580,667
        -447,-329,318
        -584,868,-557
        544,-627,-890
        564,392,-477
        455,729,728
        -892,524,684
        -689,845,-530
        423,-701,434
        7,-33,-71
        630,319,-379
        443,580,662
        -789,900,-551
        459,-707,401

        --- scanner 1 ---
        686,422,578
        605,423,415
        515,917,-361
        -336,658,858
        95,138,22
        -476,619,847
        -340,-569,-846
        567,-361,727
        -460,603,-452
        669,-402,600
        729,430,532
        -500,-761,534
        -322,571,750
        -466,-666,-811
        -429,-592,574
        -355,545,-477
        703,-491,-529
        -328,-685,520
        413,935,-424
        -391,539,-444
        586,-435,557
        -364,-763,-893
        807,-499,-711
        755,-354,-619
        553,889,-390

        --- scanner 2 ---
        649,640,665
        682,-795,504
        -784,533,-524
        -644,584,-595
        -588,-843,648
        -30,6,44
        -674,560,763
        500,723,-460
        609,671,-379
        -555,-800,653
        -675,-892,-343
        697,-426,-610
        578,704,681
        493,664,-388
        -671,-858,530
        -667,343,800
        571,-461,-707
        -138,-166,112
        -889,563,-600
        646,-828,498
        640,759,510
        -630,509,768
        -681,-892,-333
        673,-379,-804
        -742,-814,-386
        577,-820,562

        --- scanner 3 ---
        -589,542,597
        605,-692,669
        -500,565,-823
        -660,373,557
        -458,-679,-417
        -488,449,543
        -626,468,-788
        338,-750,-386
        528,-832,-391
        562,-778,733
        -938,-730,414
        543,643,-506
        -524,371,-870
        407,773,750
        -104,29,83
        378,-903,-323
        -778,-728,485
        426,699,580
        -438,-605,-362
        -469,-447,-387
        509,732,623
        647,635,-688
        -868,-804,481
        614,-800,639
        595,780,-596

        --- scanner 4 ---
        727,592,562
        -293,-554,779
        441,611,-461
        -714,465,-776
        -743,427,-804
        -660,-479,-426
        832,-632,460
        927,-485,-438
        408,393,-506
        466,436,-512
        110,16,151
        -258,-428,682
        -393,719,612
        -211,-452,876
        808,-476,-593
        -575,615,604
        -485,667,467
        -680,325,-822
        -627,-443,-432
        872,-547,-609
        833,512,582
        807,604,487
        839,-516,451
        891,-625,532
        -652,-548,-490
        30,-46,-14
        """.trimIndent().split('\n')
    println(day19Part1(testInput))
    println(day19Part1(getInput(19)))
    println(day19Part2(testInput))
    println(day19Part2(getInput(19)))
}

fun day19Part1(lines: List<String>): Any {
    val scanners = parseInput(lines)
    val count = scanners.size
    val firstScanner = scanners.removeAt(0)
    val alignedScanners = mutableListOf(firstScanner)
    while (alignedScanners.size < count) {
        outer@ for (unalignedScanner in scanners) {
            for (alignedScanner in alignedScanners) {
                val (_, relativeOtherPoints) = alignedScanner.alignOtherWithThis(unalignedScanner) ?: continue
                // We managed to align this scanner
                scanners.remove(unalignedScanner)
                alignedScanners.add(Scanner(relativeOtherPoints))
                println("Aligned ${alignedScanners.size} so far")
                break@outer
            }
        }
    }
    return alignedScanners.flatMap { it.points }.toSet().size
}

fun day19Part2(lines: List<String>): Any {
    val scanners = parseInput(lines)
    val count = scanners.size
    val firstScanner = scanners.removeAt(0)
    val alignedScanners = mutableListOf(firstScanner)
    val offsets = mutableSetOf<Offset>()
    while (alignedScanners.size < count) {
        outer@ for (unalignedScanner in scanners) {
            for (alignedScanner in alignedScanners) {
                val (offset, relativeOtherPoints) = alignedScanner.alignOtherWithThis(unalignedScanner) ?: continue
                // We managed to align this scanner
                offsets.add(offset)
                scanners.remove(unalignedScanner)
                alignedScanners.add(Scanner(relativeOtherPoints))
                println("Aligned ${alignedScanners.size} so far")
                break@outer
            }
        }
    }
    var maxDistance = Int.MIN_VALUE
    for (offset1 in offsets) {
        for (offset2 in offsets) {
            val (x1, y1, z1) = offset1.distanceOffsets
            val (x2, y2, z2) = offset2.distanceOffsets
            val dist = abs(x1 - x2) + abs(y1 - y2) + abs(z1 - z2)
            maxDistance = maxOf(maxDistance, dist)
        }
    }
    return maxDistance
}

fun point(x: Int, y: Int, z: Int) = mk.ndarray(mk[x, y, z])

val Point.x get() = this[0]
val Point.y get() = this[1]
val Point.z get() = this[2]

typealias Point = D1Array<Int>

private class Scanner(val points: Set<Point>) {
    // Remember all the scanners which do not align with this
    private val failedAlignments = mutableSetOf<Scanner>()

    fun alignOtherWithThis(other: Scanner): Pair<Offset, Set<Point>>? {
        if (other in failedAlignments) {
            return null
        }

        for (rotation in allRotationMatrices) {
            for (otherPoint in other.points) {
                val rotatedOtherPoint = rotation dot otherPoint
                for (thisPoint in points) {
                    // Given the current rotation of the other scanner relative to us,
                    // we want to align this point with the other point, and see if the other points align
                    val xOffset = thisPoint.x - rotatedOtherPoint.x
                    val yOffset = thisPoint.y - rotatedOtherPoint.y
                    val zOffset = thisPoint.z - rotatedOtherPoint.z
                    val relativeOtherPoints = other.pointsWithOffsetApplied(xOffset, yOffset, zOffset, rotation)

                    if ((points intersect relativeOtherPoints).size >= 12) {
                        return Offset(Triple(xOffset, yOffset, zOffset), rotation) to relativeOtherPoints
                    }
                }
            }
        }

        failedAlignments.add(other)
        return null
    }

    fun pointsWithOffsetApplied(xOffset: Int, yOffset: Int, zOffset: Int, rotation: D2Array<Int>): Set<Point> =
        buildSet {
            for (point in points) {
                add((rotation dot point).let {
                    point(
                        it.x + xOffset,
                        it.y + yOffset,
                        it.z + zOffset
                    )
                })
            }
        }
}

private data class Offset(val distanceOffsets: Triple<Int, Int, Int>, val rotationMatrix: D2Array<Int>)

private enum class Axis {
    X {
        override val matrix = mk.ndarray(
            mk[
                    mk[1, 0, 0],
                    mk[0, 0, -1],
                    mk[0, 1, 0]
            ]
        )
    },
    Y {
        override val matrix = mk.ndarray(
            mk[
                    mk[0, 0, 1],
                    mk[0, 1, 0],
                    mk[-1, 0, 0]
            ]
        )
    },
    Z {
        override val matrix = mk.ndarray(
            mk[
                    mk[0, -1, 0],
                    mk[1, 0, 0],
                    mk[0, 0, 1]
            ]
        )
    };

    abstract val matrix: D2Array<Int>
}

private fun rotationMatrix(axis: Axis, times: Int) = mk.linalg.pow(axis.matrix, times)

private fun rotations4(axis: Axis) = (0..3).map { rotationMatrix(axis, it) }

private val allRotationMatrices = getAllRotationMatrices().toList()

private fun getAllRotationMatrices() = sequence {
    yieldAll(rotations4(Axis.X))
    yieldAll(rotations4(Axis.X).map { it dot rotationMatrix(Axis.Y, 2) })
    yieldAll(rotations4(Axis.Z).map { it dot rotationMatrix(Axis.Y, 1) })
    yieldAll(rotations4(Axis.Z).map { it dot rotationMatrix(Axis.Y, 3) })
    yieldAll(rotations4(Axis.Y).map { it dot rotationMatrix(Axis.Z, 1) })
    yieldAll(rotations4(Axis.Y).map { it dot rotationMatrix(Axis.Z, 3) })
}

private fun parseInput(lines: List<String>): MutableList<Scanner> {
    val scanners = mutableListOf<Scanner>()
    var current = mutableSetOf<Point>()
    for (line in lines) {
        if (line.startsWith("---")) continue
        if (line.isBlank()) {
            scanners.add(Scanner(current))
            current = mutableSetOf()
        } else {
            val (x, y, z) = line.split(',').map { it.toInt() }
            current.add(point(x, y, z))
        }
    }
    require(current.isNotEmpty())
    scanners.add(Scanner(current))
    return scanners
}