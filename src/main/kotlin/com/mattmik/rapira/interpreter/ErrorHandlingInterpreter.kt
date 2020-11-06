package com.mattmik.rapira.interpreter

import com.github.ajalt.clikt.core.ProgramResult
import com.mattmik.rapira.console.ConsoleWriter
import com.mattmik.rapira.control.ControlFlowException
import com.mattmik.rapira.errors.InterpreterRuntimeError

class ErrorHandlingInterpreter<T>(
    private val interpreter: Interpreter<T>,
    private val abortOnError: Boolean,
) : Interpreter<T> {

    override fun interpret(input: T) {
        try {
            interpreter.interpret(input)
        } catch (exception: ControlFlowException) {
            ConsoleWriter.printError(exception.illegalUsageMessage, exception.token)
            abortIfNeeded()
        } catch (error: InterpreterRuntimeError) {
            ConsoleWriter.printError(error.message, error.token)
            abortIfNeeded()
        }
    }

    private fun abortIfNeeded() {
        if (abortOnError) {
            throw ProgramResult(statusCode = 1)
        }
    }
}
