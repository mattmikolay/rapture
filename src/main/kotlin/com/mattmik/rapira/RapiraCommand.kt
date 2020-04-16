package com.mattmik.rapira

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.file

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
        echo("Received file ${inputFile.path}")
    }
}

fun main(args: Array<String>) = RapiraCommand().main(args)
