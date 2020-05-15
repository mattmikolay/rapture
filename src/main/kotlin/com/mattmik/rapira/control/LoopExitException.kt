package com.mattmik.rapira.control

import org.antlr.v4.runtime.Token

/**
 * Exception used to break out of a loop (do, for, while, or repeat).
 * This is thrown when an `exit` statement is encountered.
 */
class LoopExitException(val token: Token) : Exception()
