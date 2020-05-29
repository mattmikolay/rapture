package com.mattmik.rapira.errors

class IncorrectArgumentCountError(
    expectedArgCount: Int,
    actualArgCount: Int
) : InterpreterRuntimeError(
    """
    Incorrect number of arguments passed to callable. Expected $expectedArgCount argument(s), but received $actualArgCount.
    """.trimIndent()
)
