package com.mattmik.rapira.errors

import com.mattmik.rapira.objects.RObject
import org.antlr.v4.runtime.Token

class NonIntegerIndexError(value: RObject, token: Token) : InterpreterRuntimeError(
    "Value $value is not a valid index; index values must be positive integers",
    token
)
