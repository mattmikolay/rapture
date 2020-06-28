package com.mattmik.rapira.errors

import com.mattmik.rapira.objects.RObject
import org.antlr.v4.runtime.Token

class IllegalForLoopError(value: RObject, token: Token) : InterpreterRuntimeError(
    "Illegal initial value $value passed to for loop",
    token
)
