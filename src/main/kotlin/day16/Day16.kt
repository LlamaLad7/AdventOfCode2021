package day16

import util.getRawInput
import java.math.BigInteger

fun main() {
//    println(day16Part1("020D64AEE52E55B4C017938FBBAC2D6002A53D21F9E90C18023600B80021D0862DC1700043232C2284D3B0105007251DE33CF281802D0E7001A0958C3B6EB542D2014340010B89112E228803518E2047E0004322B4128352DFE72BFE1CC77000E226B92FF7F7F0F4899CCEB788FBA632A444019349E40A801CA941898B661ECBC40820061A78E254024C126797B31A804B27C0582B2D7D4AF02791E431531100B2458A6219D29CB6C4247F7D6DB27BCBA4065138014C05B00801CC0513280108047020106460079801000332200B60002832801C200718012801503801A800B02801723F9B90009D6600D44A87B0CC8010B89D0661F980331F20A44470076767F8EF75AA94F5E1E6E9790C9008BF801AB8002171CA2A45C100661FC508B911C8043EC00C224BB8A753A6677FDB7B8EA85932F4600BE0039138612F684AB86392889C4A201253C013100623D464834200CC1787D09E76FC78200A16603A543E6D9E695E4C74C012D004646D08CAF74391B4232BDD1E4FFEE033805B3DAB074ACF351399FCCEA5F592697E1CB802B2D1D0BCFE410C015B004E46BE17973C949C213153005A6932C0129BDF675DD2CBF3482401BE7802D37AA4DFE6F549BD4A42363A200D5F40149985FEDF2ACF35AB4BD3003004A730F74019B8803F08A0943B1007A21C2487C0002DC578BC600A497B35A8050020F24432444401415002AF07A7F7FE004DB93001A931FC33A802B37FB517A4A52254010E2374C637895BF7E5CC66F53EB0CC2F4C92080292B1E7A0DB26BE6008CE1ACC801804938F530A1227F2A6A4004349A31009F7801A900021908A18C5D100722C43C8F9312CFD4040269934949661E0096FE75092ACA4F0B6A005CD6CBE1218027258AA3F00439377F5D566E338D121C0239DD9C4942FA4E8F73DFA62656402704E523896FAE9E00B4E779DE6BF15595C56DBF0ACD391802F400FA4FEADD769FD5BAE7318FCF32AB8"))
    val testInput = """
        620080001611562C8802118E34
    """.trimIndent()
    println(day16Part1(testInput))
    println(day16Part1(getRawInput(16).trim()))
    println(day16Part2(testInput))
    println(day16Part2(getRawInput(16).trim()))
}

fun day16Part1(line: String): Any {
    val bin = line.fromHexToBin()
    val packet = parsePacket(PacketInput(bin))
    return packet.versionNumberSum()
}

fun day16Part2(line: String): Any {
    val bin = line.fromHexToBin()
    val packet = parsePacket(PacketInput(bin))
    return packet.evaluate()
}

private sealed class Packet(val versionNumber: Int, val typeID: Int) {
    abstract fun versionNumberSum(): Int
    abstract fun evaluate(): BigInteger
}

private class LiteralPacket(versionNumber: Int, input: PacketInput) : Packet(versionNumber, 4) {
    val literal: BigInteger

    init {
        var literalString = ""
        var shouldKeepReading = true
        while (shouldKeepReading) {
            val nextChunk = input.consume(5)
            if (nextChunk.first() == '0') {
                shouldKeepReading = false
            }
            literalString += nextChunk.drop(1)
        }
        literal = literalString.toBigInteger(2)
    }

    override fun versionNumberSum() = versionNumber

    override fun evaluate() = literal
}

private class OperatorPacket(versionNumber: Int, typeID: Int, input: PacketInput) : Packet(versionNumber, typeID) {
    val subPackets = mutableListOf<Packet>()

    init {
        val lengthTypeID = input.consume(1)
        if (lengthTypeID == "0") {
            val lengthInBits = input.consume(15).toInt(2)
            val subPacketsData = PacketInput(input.consume(lengthInBits))
            while (!subPacketsData.isEmpty()) {
                subPackets.add(parsePacket(subPacketsData))
            }
        } else {
            val subPacketCount = input.consume(11).toInt(2)
            repeat(subPacketCount) {
                subPackets.add(parsePacket(input))
            }
        }
    }

    override fun versionNumberSum() = versionNumber + subPackets.sumOf { it.versionNumberSum() }

    override fun evaluate(): BigInteger = when (typeID) {
        0 -> subPackets.sumOf { it.evaluate() }
        1 -> subPackets.map { it.evaluate() }.reduce { acc, i -> acc * i }
        2 -> subPackets.minOf { it.evaluate() }
        3 -> subPackets.maxOf { it.evaluate() }
        5 -> if (subPackets[0].evaluate() > subPackets[1].evaluate()) BigInteger.ONE else BigInteger.ZERO
        6 -> if (subPackets[0].evaluate() < subPackets[1].evaluate()) BigInteger.ONE else BigInteger.ZERO
        7 -> if (subPackets[0].evaluate() == subPackets[1].evaluate()) BigInteger.ONE else BigInteger.ZERO
        else -> error("Illegal type id for operator packet: $typeID")
    }
}

private class PacketInput(var data: String) {
    fun consume(bits: Int) = data.take(bits).also { data = data.drop(bits) }
    fun isEmpty() = data.isEmpty()
}

private fun parsePacket(input: PacketInput): Packet {
    val versionNumber = input.consume(3).toInt(2)
    val typeID = input.consume(3).toInt(2)

    return if (typeID == 4) {
        LiteralPacket(versionNumber, input)
    } else {
        OperatorPacket(versionNumber, typeID, input)
    }
}

private fun String.fromHexToBin() = buildString {
    for (char in this@fromHexToBin) {
        append(char.digitToInt(16).toString(2).padStart(4, padChar = '0'))
    }
}