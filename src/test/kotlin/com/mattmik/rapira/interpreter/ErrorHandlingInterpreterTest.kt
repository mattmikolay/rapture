package com.mattmik.rapira.interpreter

import com.github.ajalt.clikt.core.ProgramResult
import com.mattmik.rapira.console.ConsoleWriter
import com.mattmik.rapira.control.LoopExitException
import com.mattmik.rapira.errors.IllegalReturnValueError
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.mockk.Called
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import org.antlr.v4.runtime.Token

private const val TEST_ERROR_MESSAGE = "Error!"
private const val TEST_ERROR_LINE = 12
private const val TEST_ERROR_CHAR_POSITION_IN_LINE = 25

class ErrorHandlingInterpreterTest : WordSpec({

    beforeTest {
        mockkObject(ConsoleWriter)
    }

    afterTest {
        unmockkObject(ConsoleWriter)
    }

    fun makeMockToken(): Token =
        mockk {
            every { line } returns TEST_ERROR_LINE
            every { charPositionInLine } returns TEST_ERROR_CHAR_POSITION_IN_LINE
        }

    "interpret" When {

        "abortOnError is true" should {
            val abortOnError = true

            "not terminate program when no exception is thrown" {
                val mockInterpreter = mockk<Interpreter<Unit>> {
                    every { interpret(any()) } just Runs
                }

                val errorHandlingInterpreter =
                    ErrorHandlingInterpreter(mockInterpreter, abortOnError)

                shouldNotThrow<ProgramResult> { errorHandlingInterpreter.interpret(Unit) }
                verify(exactly = 1) { mockInterpreter.interpret(Unit) }
                verify { ConsoleWriter wasNot Called }
            }

            "terminate program when ControlFlowException thrown" {
                val mockToken = makeMockToken()
                val testControlFlowException = LoopExitException(mockToken)
                val mockInterpreter = mockk<Interpreter<Unit>> {
                    every { interpret(any()) } throws testControlFlowException
                }

                val errorHandlingInterpreter =
                    ErrorHandlingInterpreter(mockInterpreter, abortOnError)

                val programResult = shouldThrow<ProgramResult> { errorHandlingInterpreter.interpret(Unit) }
                programResult.statusCode shouldBeExactly 1
                verify(exactly = 1) {
                    mockInterpreter.interpret(Unit)
                    ConsoleWriter.printError(testControlFlowException.illegalUsageMessage, mockToken)
                }
            }

            "terminate program when InterpreterRuntimeError thrown" {
                val mockToken = makeMockToken()
                val testInterpreterRuntimeError = IllegalReturnValueError(mockToken)
                val mockInterpreter = mockk<Interpreter<Unit>> {
                    every { interpret(any()) } throws testInterpreterRuntimeError
                }

                val errorHandlingInterpreter =
                    ErrorHandlingInterpreter(mockInterpreter, abortOnError)

                val programResult = shouldThrow<ProgramResult> { errorHandlingInterpreter.interpret(Unit) }
                programResult.statusCode shouldBeExactly 1
                verify(exactly = 1) {
                    mockInterpreter.interpret(Unit)
                    ConsoleWriter.printError(testInterpreterRuntimeError.message, mockToken)
                }
            }

            "terminate program when SyntaxError thrown" {
                val testSyntaxError = SyntaxError(TEST_ERROR_MESSAGE, TEST_ERROR_LINE, TEST_ERROR_CHAR_POSITION_IN_LINE)
                val mockInterpreter = mockk<Interpreter<Unit>> {
                    every { interpret(any()) } throws testSyntaxError
                }

                val errorHandlingInterpreter =
                    ErrorHandlingInterpreter(mockInterpreter, abortOnError)

                val programResult = shouldThrow<ProgramResult> { errorHandlingInterpreter.interpret(Unit) }
                programResult.statusCode shouldBeExactly 1
                verify(exactly = 1) {
                    mockInterpreter.interpret(Unit)
                    ConsoleWriter.printError(testSyntaxError.message, TEST_ERROR_LINE, TEST_ERROR_CHAR_POSITION_IN_LINE)
                }
            }
        }

        "abortOnError is false" should {
            val abortOnError = false

            "not terminate program when no exception is thrown" {
                val mockInterpreter = mockk<Interpreter<Unit>> {
                    every { interpret(any()) } just Runs
                }

                val errorHandlingInterpreter =
                    ErrorHandlingInterpreter(mockInterpreter, abortOnError)

                shouldNotThrow<ProgramResult> { errorHandlingInterpreter.interpret(Unit) }
                verify(exactly = 1) { mockInterpreter.interpret(Unit) }
                verify { ConsoleWriter wasNot Called }
            }

            "not terminate program when ControlFlowException thrown" {
                val mockToken = makeMockToken()
                val testControlFlowException = LoopExitException(mockToken)
                val mockInterpreter = mockk<Interpreter<Unit>> {
                    every { interpret(any()) } throws testControlFlowException
                }

                val errorHandlingInterpreter =
                    ErrorHandlingInterpreter(mockInterpreter, abortOnError)

                shouldNotThrow<ProgramResult> { errorHandlingInterpreter.interpret(Unit) }
                verify(exactly = 1) {
                    mockInterpreter.interpret(Unit)
                    ConsoleWriter.printError(testControlFlowException.illegalUsageMessage, mockToken)
                }
            }

            "not terminate program when InterpreterRuntimeError thrown" {
                val mockToken = makeMockToken()
                val testInterpreterRuntimeError = IllegalReturnValueError(mockToken)
                val mockInterpreter = mockk<Interpreter<Unit>> {
                    every { interpret(any()) } throws testInterpreterRuntimeError
                }

                val errorHandlingInterpreter =
                    ErrorHandlingInterpreter(mockInterpreter, abortOnError)

                shouldNotThrow<ProgramResult> { errorHandlingInterpreter.interpret(Unit) }
                verify(exactly = 1) {
                    mockInterpreter.interpret(Unit)
                    ConsoleWriter.printError(testInterpreterRuntimeError.message, mockToken)
                }
            }

            "not terminate program when SyntaxError thrown" {
                val testSyntaxError = SyntaxError(TEST_ERROR_MESSAGE, TEST_ERROR_LINE, TEST_ERROR_CHAR_POSITION_IN_LINE)
                val mockInterpreter = mockk<Interpreter<Unit>> {
                    every { interpret(any()) } throws testSyntaxError
                }

                val errorHandlingInterpreter =
                    ErrorHandlingInterpreter(mockInterpreter, abortOnError)

                shouldNotThrow<ProgramResult> { errorHandlingInterpreter.interpret(Unit) }
                verify(exactly = 1) {
                    mockInterpreter.interpret(Unit)
                    ConsoleWriter.printError(testSyntaxError.message, TEST_ERROR_LINE, TEST_ERROR_CHAR_POSITION_IN_LINE)
                }
            }
        }
    }
})
