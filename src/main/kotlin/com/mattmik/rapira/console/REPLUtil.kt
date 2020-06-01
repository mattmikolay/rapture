package com.mattmik.rapira.console

import com.github.ajalt.clikt.output.TermUi.echo
import com.mattmik.rapira.VERSION

fun printREPLHeader() {
    echo("ReRap3 v$VERSION")
    echo("Type \"quit\" to exit")
}

fun readREPLStatement(): String? {
    echo(">>> ", trailingNewline = false)
    return readLine()
}
