package com.mattmik.rapira.errors

import com.github.ajalt.clikt.core.ProgramResult
import com.mattmik.rapira.console.ConsoleWriter
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
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

    "syntaxError" When {
        "abortOnError is true" should {
            val abortOnError = true

            "throw ProgramResult exception" {
                val listener = SyntaxErrorListener(abortOnError)

                shouldThrow<ProgramResult> {
                    listener.syntaxError(
                        recognizer = null,
                        offendingSymbol = null,
                        line = TEST_LINE,
                        charPositionInLine = TEST_CHAR_POSITION_IN_LINE,
                        msg = TEST_MESSAGE,
                        e = null
                    )
                }

                verify(exactly = 1) {
                    ConsoleWriter.printError(
                        withArg { it.contains(TEST_MESSAGE) },
                        TEST_LINE,
                        TEST_CHAR_POSITION_IN_LINE
                    )
                }
            }
        }

        "abortOnError is false" should {
            val abortOnError = false

            "not throw ProgramResult exception" {
                val listener = SyntaxErrorListener(abortOnError)

                shouldNotThrow<ProgramResult> {
                    listener.syntaxError(
                        recognizer = null,
                        offendingSymbol = null,
                        line = TEST_LINE,
                        charPositionInLine = TEST_CHAR_POSITION_IN_LINE,
                        msg = TEST_MESSAGE,
                        e = null
                    )
                }

                verify(exactly = 1) {
                    ConsoleWriter.printError(
                        withArg { it.contains(TEST_MESSAGE) },
                        TEST_LINE,
                        TEST_CHAR_POSITION_IN_LINE
                    )
                }
            }
        }
    }
})
