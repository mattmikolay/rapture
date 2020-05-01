package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll

class FunctionTest : WordSpec({
    "call" should {
        "read extern objects from old environment" {
            val mockEnvironment = mockk<Environment>()
            val extern = listOf(
                "externVariable1",
                "externVariable2",
                "externVariable3"
            )
            every { mockEnvironment[any()] } returns Empty
            val function = Function(extern = extern)

            function.call(mockEnvironment, emptyList())

            verifyAll {
                mockEnvironment["externVariable1"]
                mockEnvironment["externVariable2"]
                mockEnvironment["externVariable3"]
            }
        }

        "throw exception when param and argument count differ" {
            val params = listOf("param1", "param2", "param3")
            val arguments = listOf(
                InArgument(mockk()),
                InArgument(mockk())
            )
            val function = Function(null, params)
            shouldThrow<RapiraInvalidOperationError> {
                function.call(Environment(), arguments)
            }
        }
    }

    "toString" should {
        "return user friendly representation" {
            Function() shouldConvertToString "function"
        }
    }
})
