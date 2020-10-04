package com.mattmik.rapira.interpreter

import java.io.InputStream

object InterpreterFactory {

    fun makeInputStreamInterpreter(): Interpreter<InputStream> {
        val charStreamInterpreter = CharStreamInterpreter()
        val errorHandlingInterpreter =
            ErrorHandlingInterpreter(charStreamInterpreter, abortOnError = true)
        return InputStreamInterpreter(errorHandlingInterpreter)
    }

    fun makeREPLInterpreter(): Interpreter<Unit> {
        val charStreamInterpreter = CharStreamInterpreter()
        val errorHandlingInterpreter =
            ErrorHandlingInterpreter(charStreamInterpreter, abortOnError = false)
        return REPLInterpreter(errorHandlingInterpreter)
    }
}
