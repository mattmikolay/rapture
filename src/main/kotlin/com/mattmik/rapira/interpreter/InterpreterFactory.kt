package com.mattmik.rapira.interpreter

import com.github.ajalt.clikt.output.TermUi.echo
import com.mattmik.rapira.Environment
import com.mattmik.rapira.visitors.StatementVisitor
import java.io.InputStream

object InterpreterFactory {

    fun makeInputStreamInterpreter(): Interpreter<InputStream> {
        val statementVisitor = StatementVisitor(Environment())
        val charStreamInterpreter = CharStreamInterpreter(statementVisitor)
        val errorHandlingInterpreter =
            ErrorHandlingInterpreter(charStreamInterpreter, abortOnError = true)
        return InputStreamInterpreter(errorHandlingInterpreter)
    }

    fun makeREPLInterpreter(): Interpreter<Unit> {
        val statementVisitor = StatementVisitor(
            Environment(),
            outputToRepl = { obj -> echo(obj) },
        )
        val charStreamInterpreter = CharStreamInterpreter(statementVisitor)
        val errorHandlingInterpreter =
            ErrorHandlingInterpreter(charStreamInterpreter, abortOnError = false)
        return REPLInterpreter(errorHandlingInterpreter)
    }
}
