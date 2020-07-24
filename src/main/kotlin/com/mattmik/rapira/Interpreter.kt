package com.mattmik.rapira

import com.github.ajalt.clikt.core.ProgramResult
import com.mattmik.rapira.antlr.RapiraLexer
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.console.ConsoleWriter
import com.mattmik.rapira.control.ControlFlowException
import com.mattmik.rapira.errors.InterpreterRuntimeError
import com.mattmik.rapira.errors.SyntaxErrorListener
import com.mattmik.rapira.visitors.StatementVisitor
import java.io.InputStream
import org.antlr.v4.runtime.BailErrorStrategy
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ConsoleErrorListener
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.antlr.v4.runtime.tree.ParseTree

object Interpreter {

    private val statementVisitor = StatementVisitor(Environment())

    fun interpretStatement(statement: String) {
        val lexer = RapiraLexer(CharStreams.fromString("$statement\n"))
        val parser = makeParser(lexer)

        try {
            val tree = parser.dialogUnit()
            interpret(tree)
        } catch (exception: ParseCancellationException) {
            // no-op; syntax error message is printed by SyntaxErrorListener
        }
    }

    fun interpretInputStream(inputStream: InputStream) {
        val lexer = RapiraLexer(CharStreams.fromStream(inputStream))
        val parser = makeParser(lexer)

        val tree = try {
            parser.fileInput()
        } catch (exception: ParseCancellationException) {
            // Halt execution; syntax error message is printed by SyntaxErrorListener
            throw ProgramResult(statusCode = 1)
        }

        interpret(tree)
    }

    private fun makeParser(lexer: RapiraLexer) =
        RapiraParser(CommonTokenStream(lexer)).apply {
            removeErrorListener(ConsoleErrorListener.INSTANCE)
            addErrorListener(SyntaxErrorListener)
            // FIXME BailErrorStrategy is swallowing errors that should be reported by SyntaxErrorListener
            errorHandler = BailErrorStrategy()
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
