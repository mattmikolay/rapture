package com.mattmik.rapira.errors

import org.antlr.v4.runtime.Token

/**
 * Base exception thrown when an error occurs while interpreting one or more statements.
 * All errors raised to the user should inherit from this superclass.
 *
 * @param message a message describing the error that should be displayed to the user
 */
abstract class InterpreterRuntimeError(
    override val message: String,
    val token: Token
) : Exception(message)
