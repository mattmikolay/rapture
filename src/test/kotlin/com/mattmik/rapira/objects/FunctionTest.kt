package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.args.InOutArgument
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.errors.RapiraIncorrectArgumentCountError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.mockk.mockk

class FunctionTest : WordSpec({

    fun makeFunctionParams(vararg paramNames: String) =
        paramNames.map { paramName -> Parameter(ParamType.In, paramName) }

    "call" should {
        "throw exception when param and argument count differ" {
            val params = makeFunctionParams("param1", "param2", "param3")
            val arguments = listOf(
                InArgument(mockk()),
                InArgument(mockk())
            )
            val function = Function(null, null, params)
            shouldThrow<RapiraIncorrectArgumentCountError> {
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
