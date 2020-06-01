package com.mattmik.rapira.console

import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.output.TermUi.prompt
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.LogicalNo
import com.mattmik.rapira.objects.LogicalYes
import com.mattmik.rapira.objects.parseEscapedText
import com.mattmik.rapira.objects.toRInteger
import com.mattmik.rapira.objects.toReal
import com.mattmik.rapira.objects.toText

val integerRegex = """[+-]?[0-9]+""".toRegex()
val realRegex = """[+-]?[0-9]+(?:e[+-]?[0-9]+|\.[0-9]+(?:e[+-]?[0-9]+)?)""".toRegex()
val textRegex = """"(?:[^\r\n"]|"")*"""".toRegex()

object ConsoleReader {

    fun readText() =
        prompt(text = "", convert = { input -> input.toText() })

    // TODO Add sequence input?
    fun readObject() =
        prompt(
            text = "",
            convert = { input -> parseObject(input.trim()) }
        )

    fun parseObject(input: String) = when {
        input == "empty" -> Empty
        input == "yes" -> LogicalYes
        input == "no" -> LogicalNo
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
