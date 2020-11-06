package com.mattmik.rapira.interpreter

import com.mattmik.rapira.antlr.RapiraVisitor
import io.kotest.core.spec.style.WordSpec
import io.mockk.Called
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.antlr.v4.runtime.CharStreams

private const val TEST_VALID_STATEMENT = "output: \"Test!\""
private const val TEST_INVALID_STATEMENT = "output: |||"

class CharStreamInterpreterTest : WordSpec({

    val mockVisitor = mockk<RapiraVisitor<Unit>>()

    beforeTest {
        every { mockVisitor.visit(any()) } just Runs
    }

    afterTest {
        clearAllMocks()
    }

    "interpret" When {
        "no syntax errors occurred" should {
            "visit tree using StatementVisitor" {
                val testCharStream = CharStreams.fromString(TEST_VALID_STATEMENT)
                CharStreamInterpreter(mockVisitor).interpret(testCharStream)
                verify(exactly = 1) {
                    mockVisitor.visit(any())
                }
            }
        }

        "syntax errors occurred" should {
            "not visit tree using StatementVisitor" {
                val testCharStream = CharStreams.fromString(TEST_INVALID_STATEMENT)
                CharStreamInterpreter(mockVisitor).interpret(testCharStream)
                verify { mockVisitor wasNot Called }
            }
        }
    }
})
