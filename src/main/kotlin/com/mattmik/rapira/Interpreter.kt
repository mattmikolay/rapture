package com.mattmik.rapira

import com.mattmik.rapira.antlr.RapiraLangLexer
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.control.CallableReturnException
import com.mattmik.rapira.control.LoopExitException
import com.mattmik.rapira.errors.RapiraRuntimeError
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
        val lexer = RapiraLangLexer(CharStreams.fromString("$statement\n"))
        val parser = makeParser(lexer)

        val tree = parser.dialogUnit()
        if (tree.exception == null) {
            interpret(tree)
        }
    }

    fun interpretInputStream(inputStream: InputStream) {
        val lexer = RapiraLangLexer(CharStreams.fromStream(inputStream))
        val parser = makeParser(lexer)

        val tree = parser.fileInput()
        tree.exception?.let {
            exitProcess(1)
        }

        interpret(tree)
    }

    private fun makeParser(lexer: RapiraLangLexer) =
        RapiraLangParser(CommonTokenStream(lexer)).apply {
            removeErrorListener(ConsoleErrorListener.INSTANCE)
            addErrorListener(SyntaxErrorListener)
        }

    private fun interpret(parseTree: ParseTree) {
        try {
            statementVisitor.visit(parseTree)
        } catch (exception: CallableReturnException) {
            ConsoleWriter.printError(
                "Cannot invoke return statement outside of procedure or function",
                line = exception.token.line,
                charPositionInLine = exception.token.charPositionInLine
            )
        } catch (exception: LoopExitException) {
            ConsoleWriter.printError(
                "Cannot invoke exit statement outside of loop",
                line = exception.token.line,
                charPositionInLine = exception.token.charPositionInLine
            )
        } catch (error: RapiraRuntimeError) {
            // TODO Display correct line and character of error
            ConsoleWriter.printError(
                "${error.message}",
                line = 0,
                charPositionInLine = 0
            )
        }
    }
}
