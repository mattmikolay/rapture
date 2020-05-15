package com.mattmik.rapira.control

import com.mattmik.rapira.objects.RObject
import java.lang.Exception
import org.antlr.v4.runtime.Token

/**
 * Exception used to return from a procedure or function.
 * This is thrown when a `return` statement is encountered.
 */
class CallableReturnException(
    val returnValue: RObject?,
    val token: Token
) : Exception()
