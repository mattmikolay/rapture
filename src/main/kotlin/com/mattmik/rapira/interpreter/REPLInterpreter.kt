package com.mattmik.rapira.interpreter

import com.github.ajalt.clikt.output.TermUi.echo
import com.github.ajalt.clikt.output.TermUi.prompt
import com.mattmik.rapira.VERSION
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams

private const val QUIT_STATEMENT = ":quit"

class REPLInterpreter(private val charStreamInterpreter: Interpreter<CharStream>) : Interpreter<Unit> {

    override fun interpret(input: Unit) {
        echo("Rapture v$VERSION")
        echo("Type $QUIT_STATEMENT to exit")

        var line: String?
        while (true) {
            line = prompt(text = "", promptSuffix = ">>> ")

            if (line == null || line == QUIT_STATEMENT)
                return

            val charStream = CharStreams.fromString("$line\n")
            charStreamInterpreter.interpret(charStream)
        }
    }
}
