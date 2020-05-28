package com.mattmik.rapira.console

import com.mattmik.rapira.VERSION

fun printREPLHeader() {
    println("ReRap3 v$VERSION")
    println("Type \"quit\" to exit")
}

fun readREPLStatement(): String? {
    print(">>> ")
    return readLine()
}
