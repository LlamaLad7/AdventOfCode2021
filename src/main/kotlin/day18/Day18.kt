package day18

import util.getInput
import kotlin.math.ceil

fun main() {
    val testInput = """
[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
[[[5,[2,8]],4],[5,[[9,9],0]]]
[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
[[[[5,4],[7,7]],8],[[8,3],8]]
[[9,3],[[9,9],[6,[4,9]]]]
[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
    """.trimIndent().split('\n')
    println(day18Part1(testInput))
    println(day18Part1(getInput(18)))
    println(day18Part2(testInput))
    println(day18Part2(getInput(18)))
}

fun day18Part1(lines: List<String>): Any {
    var total = parseTree(lines[0]) as Node.Pair
    for (line in lines.drop(1)) {
        val tree = parseTree(line) as Node.Pair
        total = pairOf(total, tree)
        total.reduce()
    }
    return total.magnitude()
}

fun day18Part2(lines: List<String>): Any {
    var max = Int.MIN_VALUE
    for (line1 in lines) {
        for (line2 in lines) {
            if (line1 == line2) continue
            val tree1 = parseTree(line1) as Node.Pair
            val tree2 = parseTree(line2) as Node.Pair
            val sum = pairOf(tree1, tree2)
            sum.reduce()
            max = maxOf(max, sum.magnitude())
        }
    }
    return max
}

private sealed class Node(var parent: Pair? = null) {
    fun isLeftNode() = parent?.left == this
    fun isRightNode() = parent?.right == this

    abstract fun magnitude(): Int

    class Pair(var left: Node, var right: Node, parent: Pair? = null) : Node(parent) {
        fun explode() {
            val (leftLeaf, rightLeaf) = left to right
            require(leftLeaf is Leaf && rightLeaf is Leaf)

            var currentParent: Pair? = this
            var (foundLeft, foundRight) = false to false
            while (currentParent != null) {
                if (!foundLeft && currentParent.isRightNode()) {
                    val nodeToTheLeft = currentParent.parent!!.left
                    val rightmostLeafToTheLeft: Leaf? =
                        if (nodeToTheLeft is Leaf) {
                            nodeToTheLeft
                        } else {
                            (nodeToTheLeft as Pair).rightmostLeaf()
                        }
                    if (rightmostLeafToTheLeft != null) {
                        rightmostLeafToTheLeft.value += leftLeaf.value
                        foundLeft = true
                    }
                }
                if (!foundRight && currentParent.isLeftNode()) {
                    val nodeToTheRight = currentParent.parent!!.right
                    val leftmostLeafToTheRight: Leaf? =
                        if (nodeToTheRight is Leaf) {
                            nodeToTheRight
                        } else {
                            (nodeToTheRight as Pair).leftmostLeaf()
                        }
                    if (leftmostLeafToTheRight != null) {
                        leftmostLeafToTheRight.value += rightLeaf.value
                        foundRight = true
                    }
                }
                currentParent = currentParent.parent
            }

            if (isLeftNode()) {
                parent?.left = Leaf(0, parent)
            } else {
                parent?.right = Leaf(0, parent)
            }
        }

        fun leftmostPairNestedInFourPairs(nestingLevel: Int = 0): Pair? {
            if (nestingLevel == 4) {
                require(left is Leaf && right is Leaf)
                return this
            }
            return (left as? Pair)?.leftmostPairNestedInFourPairs(nestingLevel + 1)
                ?: (right as? Pair)?.leftmostPairNestedInFourPairs(nestingLevel + 1)
        }

        fun leftmostLeafWith10OrGreater(): Leaf? {
            return (left as? Leaf)?.takeIf { it.value >= 10 }
                ?: (left as? Pair)?.leftmostLeafWith10OrGreater()
                ?: (right as? Leaf)?.takeIf { it.value >= 10 }
                ?: (right as? Pair)?.leftmostLeafWith10OrGreater()
        }

        fun leftmostLeaf(): Leaf? {
            return left as? Leaf
                ?: (left as? Pair)?.leftmostLeaf()
                ?: right as? Leaf
                ?: (right as? Pair)?.leftmostLeaf()
        }

        fun rightmostLeaf(): Leaf? {
            return right as? Leaf
                ?: (right as? Pair)?.rightmostLeaf()
                ?: left as? Leaf
                ?: (left as? Pair)?.rightmostLeaf()
        }

        fun reduce() {
            while (true) {
                val pairToExplode = leftmostPairNestedInFourPairs()
                if (pairToExplode != null) {
                    pairToExplode.explode()
                    continue
                }
                val leafToSplit = leftmostLeafWith10OrGreater()
                if (leafToSplit != null) {
                    leafToSplit.split()
                    continue
                }
                break
            }
        }

        override fun magnitude() = left.magnitude() * 3 + right.magnitude() * 2

        override fun toString() = "[$left,$right]"
    }

    class Leaf(var value: Int, parent: Pair? = null) : Node(parent) {
        fun split() {
            val pair = pairOf(Leaf(value / 2), Leaf(ceil(value.toFloat() / 2).toInt()))
            pair.parent = parent
            if (isLeftNode()) {
                parent?.left = pair
            } else {
                parent?.right = pair
            }
        }

        override fun magnitude() = value

        override fun toString() = value.toString()
    }
}

private fun pairOf(left: Node, right: Node) = Node.Pair(left, right).also {
    left.parent = it
    right.parent = it
}

private fun parseTree(str: String): Node {
    if (str.startsWith('[')) {
        val contents = str.drop(1).dropLast(1)
        val (left, right) = splitPairString(contents)
        val leftNode = parseTree(left)
        val rightNode = parseTree(right)
        return pairOf(leftNode, rightNode)
    } else {
        return Node.Leaf(str.toInt())
    }
}

private fun splitPairString(contents: String): Pair<String, String> {
    var openCount = 0
    for ((charIndex, char) in contents.withIndex()) {
        when {
            char == '[' -> openCount++
            char == ']' -> openCount--
            char == ',' && openCount == 0 -> {
                return contents.take(charIndex) to contents.drop(charIndex + 1)
            }
        }
    }
    error("A")
}