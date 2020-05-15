package com.mattmik.rapira.control

import org.antlr.v4.runtime.Token

/**
 * Base exception used to signal a deviation from normal program control flow.
 *
 * @property illegalUsageMessage error message shown to user when an attempt to alter control flow is invalid
 * @property token the lexical token that caused this exception
 */
abstract class ControlFlowException(
    val illegalUsageMessage: String,
    val token: Token
) : Exception(illegalUsageMessage)
