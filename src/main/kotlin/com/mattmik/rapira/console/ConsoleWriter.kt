package com.mattmik.rapira.console

import com.github.ajalt.clikt.output.TermUi.echo
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.Text
import org.antlr.v4.runtime.Token

object ConsoleWriter {

    fun println(message: String) =
        echo(message)

    fun printObjects(objects: List<RObject>, lineBreak: Boolean) {
        val formattedOutput = objects.joinToString(
            separator = "",
            transform = { obj -> formatObject(obj) }
        )
        echo(message = formattedOutput, trailingNewline = lineBreak)
    }

    fun printError(message: String, token: Token) =
        printError(message, token.line, token.charPositionInLine)

    fun printError(message: String, line: Int, charPositionInLine: Int) =
        echo(message = "Error @ line $line:$charPositionInLine\n\t$message", err = true)

    /**
     * Returns a formatted string representation of a Rapira object [obj] for
     * use with the the `output` statement. This differs from [toString], which
     * returns the system's representation of the object as a string.
     */
    fun formatObject(obj: RObject) = when (obj) {
        is Text -> obj.value
        else -> obj.toString()
    }
}
