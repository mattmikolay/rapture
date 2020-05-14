package com.mattmik.rapira

import com.mattmik.rapira.antlr.RapiraLangLexer
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.control.CallableReturnException
import com.mattmik.rapira.control.LoopExitException
import com.mattmik.rapira.errors.RapiraRuntimeError
import com.mattmik.rapira.visitors.StatementVisitor
import java.io.InputStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree

class Interpreter {

    private var environment = Environment()
    private val statementVisitor = StatementVisitor(environment)

    fun interpretStatement(statement: String) {
        val lexer = RapiraLangLexer(CharStreams.fromString("$statement\n"))
        val parser = RapiraLangParser(CommonTokenStream(lexer))
        val tree = parser.dialogUnit()
        interpretTree(tree)
    }

    fun interpretInputStream(inputStream: InputStream) {
        val lexer = RapiraLangLexer(CharStreams.fromStream(inputStream))
        val parser = RapiraLangParser(CommonTokenStream(lexer))
        val tree = parser.fileInput()
        interpretTree(tree)
    }

    private fun interpretTree(parseTree: ParseTree) {
        try {
            statementVisitor.visit(parseTree)
        } catch (exception: CallableReturnException) {
            ConsoleWriter.printError("cannot invoke return outside of procedure or function")
        } catch (exception: LoopExitException) {
            ConsoleWriter.printError("cannot invoke exit outside of loop")
        } catch (error: RapiraRuntimeError) {
            ConsoleWriter.printError("${error.message}")
        }
    }
}
