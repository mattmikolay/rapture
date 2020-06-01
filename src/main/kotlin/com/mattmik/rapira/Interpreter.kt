package com.mattmik.rapira

import com.mattmik.rapira.antlr.RapiraLexer
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.console.ConsoleWriter
import com.mattmik.rapira.control.ControlFlowException
import com.mattmik.rapira.errors.InterpreterRuntimeError
import com.mattmik.rapira.errors.SyntaxErrorListener
import com.mattmik.rapira.visitors.StatementVisitor
import java.io.InputStream
import kotlin.system.exitProcess
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ConsoleErrorListener
import org.antlr.v4.runtime.tree.ParseTree

object Interpreter {

    private val statementVisitor = StatementVisitor(Environment())

    fun interpretStatement(statement: String) {
        val lexer = RapiraLexer(CharStreams.fromString("$statement\n"))
        val parser = makeParser(lexer)

        val tree = parser.dialogUnit()
        if (tree.exception == null) {
            interpret(tree)
        }
    }

    fun interpretInputStream(inputStream: InputStream) {
        val lexer = RapiraLexer(CharStreams.fromStream(inputStream))
        val parser = makeParser(lexer)

        val tree = parser.fileInput()
        tree.exception?.let {
            exitProcess(1)
        }

        interpret(tree)
    }

    private fun makeParser(lexer: RapiraLexer) =
        RapiraParser(CommonTokenStream(lexer)).apply {
            removeErrorListener(ConsoleErrorListener.INSTANCE)
            addErrorListener(SyntaxErrorListener)
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
