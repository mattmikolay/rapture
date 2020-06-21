package com.mattmik.rapira.errors

import com.mattmik.rapira.objects.RObject
import org.antlr.v4.runtime.Token

class IllegalRepeatLoopError(value: RObject, token: Token) : InterpreterRuntimeError(
    "Illegal initial value $value passed to repeat loop",
    token
)
