package com.mattmik.rapira.errors

class RapiraIncorrectArgumentCountError(
    expectedArgCount: Int,
    actualArgCount: Int
) : RapiraRuntimeError(
    """
    Incorrect number of arguments passed to callable. Expected $expectedArgCount argument(s), but received $actualArgCount.
    """.trimIndent()
)
