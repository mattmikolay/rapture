package com.mattmik.rapira.interpreter

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.should
import io.kotest.matchers.types.beOfType
import org.antlr.v4.runtime.CharStream

class InterpreterFactoryTest : WordSpec({

    "makeInputStreamInterpreter" should {
        "return instance of InputStreamInterpreter" {
            InterpreterFactory.makeInputStreamInterpreter() should beOfType<InputStreamInterpreter>()
        }
    }

    "makeREPLInterpreter" should {
        "return instance of REPLInterpreter" {
            InterpreterFactory.makeREPLInterpreter() should beOfType<REPLInterpreter<CharStream>>()
        }
    }
})
