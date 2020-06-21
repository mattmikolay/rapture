package com.mattmik.rapira.errors

import org.antlr.v4.runtime.Token

class IllegalProcedureInvocationError(token: Token) : InterpreterRuntimeError(
    "Cannot invoke procedure within expression",
    token
)
