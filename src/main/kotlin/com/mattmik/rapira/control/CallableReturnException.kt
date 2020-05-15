package com.mattmik.rapira.control

import com.mattmik.rapira.objects.RObject
import org.antlr.v4.runtime.Token

/**
 * Exception used to return from a procedure or function.
 * This is thrown when a `return` statement is encountered.
 */
class CallableReturnException(val returnValue: RObject?, token: Token) : ControlFlowException(
    illegalUsageMessage = "Cannot invoke return statement outside of procedure or function",
    token = token
)
