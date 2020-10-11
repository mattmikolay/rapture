package com.mattmik.rapira.interpreter

import com.mattmik.rapira.antlr.RapiraLexer
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.antlr.RapiraVisitor
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ConsoleErrorListener

class CharStreamInterpreter(private val visitor: RapiraVisitor<Unit>) : Interpreter<CharStream> {

    override fun interpret(input: CharStream) {
        val lexer = makeLexer(input)
        val parser = makeParser(lexer)
        val tree = parser.fileInput() // TODO REPL should use dialogUnit
        visitor.visit(tree)
    }

    private fun makeLexer(charStream: CharStream) =
        RapiraLexer(charStream).apply {
            removeErrorListener(ConsoleErrorListener.INSTANCE)
            addErrorListener(SyntaxErrorListener)
        }

    private fun makeParser(lexer: RapiraLexer) =
        RapiraParser(CommonTokenStream(lexer)).apply {
            removeErrorListener(ConsoleErrorListener.INSTANCE)
            addErrorListener(SyntaxErrorListener)
        }
}
