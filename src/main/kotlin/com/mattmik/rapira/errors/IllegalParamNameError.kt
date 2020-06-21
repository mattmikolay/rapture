package com.mattmik.rapira.errors

import org.antlr.v4.runtime.Token

class IllegalParamNameError(paramName: String, token: Token) : InterpreterRuntimeError(
    "Param name $paramName is reserved word",
    token
)
