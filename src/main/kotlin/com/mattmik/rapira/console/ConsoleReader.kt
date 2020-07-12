package com.mattmik.rapira.console

import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.output.TermUi.prompt
import com.mattmik.rapira.CONST_NO
import com.mattmik.rapira.CONST_YES
import com.mattmik.rapira.objects.*

val integerRegex = """[+-]?[0-9]+""".toRegex()
val realRegex = """[+-]?[0-9]+(?:e[+-]?[0-9]+|\.[0-9]+(?:e[+-]?[0-9]+)?)""".toRegex()
val textRegex = """"(?:[^\r\n"]|"")*"""".toRegex()

/**
 * Convenience object used to read [RObject] values from the terminal.
 */
object ConsoleReader {

    fun readText() =
        prompt(text = "", convert = { input -> input.toText() })

    // TODO Add sequence input?
    fun readObject() =
        prompt(
            text = "",
            convert = { input -> parseObject(input.trim()) }
        )

    fun parseObject(input: String): RObject = when {
        input == "empty" -> Empty
        input == "yes" -> CONST_YES
        input == "no" -> CONST_NO
        input.matches(textRegex) -> parseEscapedText(input)
        input.matches(realRegex) -> input.toDouble().toReal()
        input.matches(integerRegex) -> input.toInt().toRInteger()
        else -> throw UsageError(
            """
                Invalid object input. Please enter a valid object notation. For example:
                    - Integers: 1, 10, 1000, -1, -10, -1000
                    - Reals: 1.23, 1e10, -1.23, -1e10
                    - Text: "Hello, world!"
                    - Logical: yes, no
                    - empty
            """.trimIndent()
        )
    }
}
