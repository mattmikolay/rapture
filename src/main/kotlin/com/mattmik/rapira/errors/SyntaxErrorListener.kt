package com.mattmik.rapira.errors

import com.mattmik.rapira.ConsoleWriter.printError
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

object SyntaxErrorListener : BaseErrorListener() {

    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException?
    ) = printError(
        "Syntax error - $msg",
        line,
        charPositionInLine
    )
}
