package com.mattmik.rapira.interpreter

/**
 * Exception thrown by [SyntaxErrorListener] when a syntax error is encountered
 * during lexing or parsing.
 */
class SyntaxError(
    override val message: String,
    val line: Int,
    val charPositionInLine: Int,
) : Exception(message)
