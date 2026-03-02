package org.example

import java.io.PrintStream
import java.nio.charset.StandardCharsets

fun main() {

    System.setOut(PrintStream(System.`out`, true, StandardCharsets.UTF_8))

    val service = JavaService()
    println(service.getGreeting("Разработчик"))


}