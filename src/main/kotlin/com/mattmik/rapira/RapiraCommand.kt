package com.mattmik.rapira

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.parameters.options.versionOption

class RapiraCommand : NoOpCliktCommand(
    name = "rapira",
    help = "ReRap 3 interpreter for the Rapira programming language"
) {
    init {
        versionOption("0.1")
    }
}

fun main(args: Array<String>) = RapiraCommand().main(args)
