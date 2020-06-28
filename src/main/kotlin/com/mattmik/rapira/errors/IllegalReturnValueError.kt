package com.mattmik.rapira.errors

import org.antlr.v4.runtime.Token

class IllegalReturnValueError(token: Token) : InterpreterRuntimeError(
    "Cannot return value within procedure",
    token
)
