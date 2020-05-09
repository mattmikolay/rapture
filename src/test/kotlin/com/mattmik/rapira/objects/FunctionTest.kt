package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.args.InOutArgument
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.variables.SimpleVariable
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll

class FunctionTest : WordSpec({

    fun makeFunctionParams(vararg paramNames: String) =
        paramNames.map { paramName -> Parameter(ParamType.In, paramName) }

    "call" should {
        "read extern objects from old environment" {
            val mockEnvironment = mockk<Environment>()
            val extern = listOf(
                "externVariable1",
                "externVariable2",
                "externVariable3"
            )
            every { mockEnvironment[any()] } returns SimpleVariable(Empty)
            val function = Function(extern = extern)

            function.call(mockEnvironment, emptyList())

            verifyAll {
                mockEnvironment["externVariable1"]
                mockEnvironment["externVariable2"]
                mockEnvironment["externVariable3"]
            }
        }

        "throw exception when param and argument count differ" {
            val params = makeFunctionParams("param1", "param2", "param3")
            val arguments = listOf(
                InArgument(mockk()),
                InArgument(mockk())
            )
            val function = Function(null, null, params)
            shouldThrow<RapiraInvalidOperationError> {
                function.call(Environment(), arguments)
            }
        }

        "throw exception when given in-out arguments" {
            val params = makeFunctionParams("param1")
            val arguments = listOf(
                InOutArgument(mockk())
            )
            val function = Function(null, null, params)
            shouldThrow<RapiraIllegalArgumentException> {
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
