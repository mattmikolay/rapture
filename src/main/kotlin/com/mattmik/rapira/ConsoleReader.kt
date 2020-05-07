package com.mattmik.rapira

import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.LogicalNo
import com.mattmik.rapira.objects.LogicalYes
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.parseEscapedText
import com.mattmik.rapira.objects.toRInteger
import com.mattmik.rapira.objects.toReal
import com.mattmik.rapira.objects.toText

val integerRegex = """[+-]?[0-9]+""".toRegex()
val realRegex = """[+-]?[0-9]+(?:e[+-]?[0-9]+|\.[0-9]+(?:e[+-]?[0-9]+)?)""".toRegex()
val textRegex = """"(?:[^\r\n"]|"")*"""".toRegex()

object ConsoleReader {

    fun readText(): Text = (readLine() ?: "").toText()

    // TODO Add sequence input?
    fun readObject(): RObject {
        var obj: RObject? = null
        do {
            val input = (readLine() ?: "").trim()

            try {
                obj = parseObject(input)
            } catch (e: Exception) {
                ConsoleWriter.printError("""
                    invalid object input. Please enter a valid object notation. For example:
                        - Integers: 1, 10, 1000, -1, -10, -1000
                        - Reals: 1.23, 1e10, -1.23, -1e10
                        - Text: "Hello, world!"
                        - Logical: yes, no
                        - empty
                """.trimIndent())
            }
        } while (obj == null)

        return obj
    }

    fun parseObject(input: String) = when {
        input == "empty" -> Empty
        input == "yes" -> LogicalYes
        input == "no" -> LogicalNo
        input.matches(textRegex) -> parseEscapedText(input)
        input.matches(realRegex) -> input.toDouble().toReal()
        input.matches(integerRegex) -> input.toInt().toRInteger()
        else -> throw Exception("Failed")
    }
}
