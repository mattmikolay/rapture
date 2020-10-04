package com.mattmik.rapira.interpreter

import com.github.ajalt.clikt.output.TermUi
import io.kotest.core.spec.style.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import org.antlr.v4.runtime.CharStream

class REPLInterpreterTest : WordSpec({

    beforeTest {
        mockkObject(TermUi)
    }

    afterTest {
        unmockkObject(TermUi)
    }

    "interpret" should {
        "prompt for input until quit statement is encountered" {
            val mockCharStreamInterpreter = mockk<Interpreter<CharStream>>(relaxed = true)
            every { TermUi.prompt(text = any(), promptSuffix = any()) } returnsMany listOf(
                "output: 123",
                "output: 456",
                "output: 789",
                ":quit",
            )

            REPLInterpreter(mockCharStreamInterpreter)
                .interpret(Unit)

            verify(exactly = 3) { mockCharStreamInterpreter.interpret(any()) }
        }
    }
})
