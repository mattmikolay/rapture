package com.mattmik.rapira.interpreter

import com.mattmik.rapira.visitors.StatementVisitor
import io.kotest.core.spec.style.WordSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkConstructor
import io.mockk.verify
import org.antlr.v4.runtime.CharStreams

private const val TEST_STATEMENT = "output: \"Test!\""

class CharStreamInterpreterTest : WordSpec({

    beforeTest {
        mockkConstructor(StatementVisitor::class)
    }

    afterTest {
        unmockkConstructor(StatementVisitor::class)
    }

    "interpret" should {
        "visit tree using StatementVisitor" {
            every { anyConstructed<StatementVisitor>().visit(any()) } just Runs
            val testCharStream = CharStreams.fromString(TEST_STATEMENT)

            CharStreamInterpreter().interpret(testCharStream)

            verify(exactly = 1) {
                anyConstructed<StatementVisitor>().visit(any())
            }
        }
    }
})
