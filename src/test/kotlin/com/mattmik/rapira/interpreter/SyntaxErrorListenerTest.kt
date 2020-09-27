package com.mattmik.rapira.interpreter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec

private const val TEST_LINE = 12
private const val TEST_CHAR_POSITION_IN_LINE = 25
private const val TEST_MESSAGE = "Error!"

class SyntaxErrorListenerTest : WordSpec({

    "syntaxError" should {
        "throw SyntaxError exception" {
            shouldThrow<SyntaxError> {
                SyntaxErrorListener.syntaxError(
                    recognizer = null,
                    offendingSymbol = null,
                    line = TEST_LINE,
                    charPositionInLine = TEST_CHAR_POSITION_IN_LINE,
                    msg = TEST_MESSAGE,
                    e = null
                )
            }
        }
    }
})
