package com.mattmik.rapira.interpreter

import com.mattmik.rapira.antlr.RapiraVisitor
import io.kotest.core.spec.style.WordSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.antlr.v4.runtime.CharStreams

private const val TEST_STATEMENT = "output: \"Test!\""

class CharStreamInterpreterTest : WordSpec({

    "interpret" should {
        "visit tree using StatementVisitor" {
            val mockVisitor = mockk<RapiraVisitor<Unit>> {
                every { visit(any()) } just Runs
            }
            val testCharStream = CharStreams.fromString(TEST_STATEMENT)

            CharStreamInterpreter(mockVisitor).interpret(testCharStream)

            verify(exactly = 1) {
                mockVisitor.visit(any())
            }
        }
    }
})
