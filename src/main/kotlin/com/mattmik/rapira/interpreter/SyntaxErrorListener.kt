package com.mattmik.rapira.interpreter

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

/**
 * Subclass of ANTLR's [BaseErrorListener] used to handle syntax errors during
 * lexing and parsing.
 */
object SyntaxErrorListener : BaseErrorListener() {

    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException?
    ) =
        throw SyntaxError("Syntax error - $msg", line, charPositionInLine)
}
