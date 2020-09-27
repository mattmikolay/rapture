package com.mattmik.rapira.interpreter

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import java.io.InputStream

class InputStreamInterpreter(private val charStreamInterpreter: Interpreter<CharStream>) : Interpreter<InputStream> {

    override fun interpret(input: InputStream) =
        input.use {
            val charStream = CharStreams.fromStream(it)
            charStreamInterpreter.interpret(charStream)
        }
}
