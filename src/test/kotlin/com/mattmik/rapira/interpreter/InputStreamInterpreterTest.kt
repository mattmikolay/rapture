package com.mattmik.rapira.interpreter

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.misc.Interval
import java.io.ByteArrayInputStream

private const val TEST_STRING = "Hello, world!"

class InputStreamInterpreterTest : WordSpec({

    "interpret" should {
        "invoke interpret on wrapped CharStream interpreter" {
            val mockCharStreamInterpreter = mockk<Interpreter<CharStream>>(relaxed = true)
            val testInputStream = ByteArrayInputStream(TEST_STRING.encodeToByteArray())

            InputStreamInterpreter(mockCharStreamInterpreter)
                .interpret(testInputStream)

            verify(exactly = 1) {
                mockCharStreamInterpreter.interpret(
                    withArg { capturedCharSet ->
                        capturedCharSet.getText(Interval(0, TEST_STRING.length)) shouldBe TEST_STRING
                    }
                )
            }
        }
    }
})
