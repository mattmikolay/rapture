package com.mattmik.rapira.errors

import org.antlr.v4.runtime.Token

class IncorrectArgumentCountError(
    expectedArgCount: Int,
    actualArgCount: Int,
    token: Token
) : InterpreterRuntimeError(
    """
    Incorrect number of arguments passed to callable. Expected $expectedArgCount argument(s), but received $actualArgCount.
    """.trimIndent(),
    token
)
