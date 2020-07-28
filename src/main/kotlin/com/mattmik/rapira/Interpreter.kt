package com.mattmik.rapira

import com.mattmik.rapira.antlr.RapiraLexer
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.console.ConsoleWriter
import com.mattmik.rapira.control.ControlFlowException
import com.mattmik.rapira.errors.InterpreterRuntimeError
import com.mattmik.rapira.errors.SyntaxErrorListener
import com.mattmik.rapira.visitors.StatementVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ConsoleErrorListener
import org.antlr.v4.runtime.tree.ParseTree
import java.io.InputStream

object Interpreter {

    private val statementVisitor = StatementVisitor(Environment())

    fun interpretStatement(statement: String) {
        val lexer = RapiraLexer(CharStreams.fromString("$statement\n"))
        val parser = makeParser(lexer, abortOnError = false)
        val tree = parser.dialogUnit()
        interpret(tree)
    }

    fun interpretInputStream(inputStream: InputStream) {
        val lexer = RapiraLexer(CharStreams.fromStream(inputStream))
        val parser = makeParser(lexer, abortOnError = true)
        val tree = parser.fileInput()
        interpret(tree)
    }

    private fun makeParser(lexer: RapiraLexer, abortOnError: Boolean) =
        RapiraParser(CommonTokenStream(lexer)).apply {
            removeErrorListener(ConsoleErrorListener.INSTANCE)
            addErrorListener(SyntaxErrorListener(abortOnError))
        }

    private fun interpret(parseTree: ParseTree) {
        try {
            statementVisitor.visit(parseTree)
        } catch (exception: ControlFlowException) {
            ConsoleWriter.printError(
                exception.illegalUsageMessage,
                exception.token
            )
        } catch (error: InterpreterRuntimeError) {
            ConsoleWriter.printError(
                error.message,
                error.token
            )
        }
    }
}
