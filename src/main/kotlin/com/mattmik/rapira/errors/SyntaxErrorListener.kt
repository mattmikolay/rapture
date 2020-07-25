package com.mattmik.rapira.errors

import com.github.ajalt.clikt.core.ProgramResult
import com.mattmik.rapira.console.ConsoleWriter.printError
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

/**
 * Subclass of ANTLR's [BaseErrorListener] used to format syntax error messages
 * during parsing. When [abortOnError] is true and a syntax error is
 * encountered, the interpreter will abort program execution.
 */
class SyntaxErrorListener(private val abortOnError: Boolean) : BaseErrorListener() {

    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException?
    ) {
        printError("Syntax error - $msg", line, charPositionInLine)
        if (abortOnError) {
            throw ProgramResult(statusCode = 1)
        }
    }
}
