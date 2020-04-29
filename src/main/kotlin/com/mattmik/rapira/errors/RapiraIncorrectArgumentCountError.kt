package com.mattmik.rapira.errors

class RapiraIncorrectArgumentCountError(
    expectedArgumentCount: Int,
    actualArgumentCount: Int
) : RapiraRuntimeError(
    """
    Incorrect number of arguments passed to callable. Expected $expectedArgumentCount arguments, but received $actualArgumentCount.
    """.trimMargin()
)
