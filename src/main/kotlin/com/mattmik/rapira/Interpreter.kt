package com.mattmik.rapira

import com.mattmik.rapira.antlr.RapiraLangLexer
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.visitors.StatementVisitor
import java.io.InputStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class Interpreter {

    private val statementVisitor = StatementVisitor()

    fun interpretStatement(statement: String) {
        val lexer = RapiraLangLexer(CharStreams.fromString("$statement\n"))
        val parser = RapiraLangParser(CommonTokenStream(lexer))
        val tree = parser.dialogUnit()
        statementVisitor.visit(tree)
    }

    fun interpretInputStream(inputStream: InputStream) {
        val lexer = RapiraLangLexer(CharStreams.fromStream(inputStream))
        val parser = RapiraLangParser(CommonTokenStream(lexer))
        val tree = parser.fileInput()
        statementVisitor.visit(tree)
    }
}
