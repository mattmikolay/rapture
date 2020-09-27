package com.mattmik.rapira.interpreter

interface Interpreter<T> {
    fun interpret(input: T)
}
