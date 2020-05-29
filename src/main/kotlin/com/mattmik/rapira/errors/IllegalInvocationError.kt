package com.mattmik.rapira.errors

import org.antlr.v4.runtime.Token

class IllegalInvocationError(token: Token) : InterpreterRuntimeError(
    "Cannot invoke object that is neither a procedure nor function",
    token
)
