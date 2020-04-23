package com.mattmik.rapira

fun printREPLHeader() {
    println("ReRap3 v$VERSION")
    println("Type \"quit\" to exit")
}

fun readREPLStatement(): String? {
    print(">>> ")
    return readLine()
}
