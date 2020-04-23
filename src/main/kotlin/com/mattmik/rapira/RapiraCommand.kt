package com.mattmik.rapira

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.file
import com.mattmik.rapira.antlr.RapiraLangLexer
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.visitors.StatementVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class RapiraCommand : CliktCommand(
    name = "rapira",
    help = "ReRap 3 interpreter for the Rapira programming language"
) {
    private val inputFile by argument(name = "file")
        .file(mustExist = true, mustBeReadable = true, canBeDir = false)

    init {
        versionOption("0.1")
    }

    override fun run() {
        inputFile.inputStream().use {
            val lexer = RapiraLangLexer(CharStreams.fromStream(it))
            val parser = RapiraLangParser(CommonTokenStream(lexer))
            val tree = parser.fileInput()
            StatementVisitor().visit(tree)
        }
    }
}

fun main(args: Array<String>) = RapiraCommand().main(args)
