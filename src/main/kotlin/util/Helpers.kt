package util

import java.io.File

private class Dummy

fun getInput(day: Int) = File(Dummy::class.java.classLoader.getResource("day$day.txt")!!.toURI()).readLines()

fun getRawInput(day: Int) = File(Dummy::class.java.classLoader.getResource("day$day.txt")!!.toURI()).readText()