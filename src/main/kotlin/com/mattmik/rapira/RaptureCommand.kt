package com.mattmik.rapira

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.file

const val VERSION = "0.1"

class RaptureCommand : CliktCommand(
    name = "rapture",
    help = "Rapture interpreter for the Rapira programming language"
) {
    private val inputFile by argument(name = "file")
        .file(mustExist = true, mustBeReadable = true, canBeDir = false)
        .optional()

    init {
        versionOption(VERSION)
    }

    override fun run() {
        inputFile?.inputStream()?.use {
            Interpreter.interpretInputStream(it)
        } ?: startREPL()
    }

    private fun startREPL() {
        echo("Rapture v$VERSION")
        echo("Type \"quit\" to exit")

        var line: String?
        while (true) {
            line = prompt(text = "", promptSuffix = ">>> ")

            if (line == null || line == "quit")
                return

            Interpreter.interpretStatement(line)
        }
    }
}

fun main(args: Array<String>) = RaptureCommand().main(args)
