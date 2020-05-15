package com.mattmik.rapira

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.file

const val VERSION = "0.1"

class RapiraCommand : CliktCommand(
    name = "rapira",
    help = "ReRap 3 interpreter for the Rapira programming language"
) {
    private val inputFile by argument(name = "file")
        .file(mustExist = true, mustBeReadable = true, canBeDir = false)
        .optional()

    init {
        versionOption(VERSION)
    }

    override fun run() {
        if (inputFile == null) {
            printREPLHeader()

            var line: String?
            while (true) {
                line = readREPLStatement()

                if (line == null || line == "quit")
                    return

                Interpreter.interpretStatement(line)
            }
        }

        inputFile?.inputStream()?.use {
            Interpreter.interpretInputStream(it)
        }
    }
}

fun main(args: Array<String>) = RapiraCommand().main(args)
