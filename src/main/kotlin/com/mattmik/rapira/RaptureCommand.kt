package com.mattmik.rapira

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.file
import com.mattmik.rapira.interpreter.InterpreterFactory
import java.io.File

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

    override fun run() =
        inputFile?.let {
            interpretFile(it)
        } ?: startREPL()

    private fun interpretFile(file: File) =
        InterpreterFactory.makeInputStreamInterpreter()
            .interpret(file.inputStream())

    private fun startREPL() =
        InterpreterFactory.makeREPLInterpreter()
            .interpret(Unit)
}

fun main(args: Array<String>) = RaptureCommand().main(args)
