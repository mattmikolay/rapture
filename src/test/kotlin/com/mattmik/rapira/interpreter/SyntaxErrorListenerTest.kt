package com.mattmik.rapira.interpreter

import com.mattmik.rapira.console.ConsoleWriter
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.string.shouldEndWith
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify

private const val TEST_LINE = 12
private const val TEST_CHAR_POSITION_IN_LINE = 25
private const val TEST_MESSAGE = "Error!"

class SyntaxErrorListenerTest : WordSpec({

    beforeTest {
        mockkObject(ConsoleWriter)
    }

    afterTest {
        unmockkObject(ConsoleWriter)
    }

    "syntaxError" should {
        "output error message to console" {
            SyntaxErrorListener.syntaxError(
                recognizer = null,
                offendingSymbol = null,
                line = TEST_LINE,
                charPositionInLine = TEST_CHAR_POSITION_IN_LINE,
                msg = TEST_MESSAGE,
                e = null
            )

            verify(exactly = 1) {
                ConsoleWriter.printError(
                    withArg { formattedErrorMessage ->
                        formattedErrorMessage.shouldEndWith(TEST_MESSAGE)
                    },
                    TEST_LINE,
                    TEST_CHAR_POSITION_IN_LINE,
                )
            }
        }
    }
})
