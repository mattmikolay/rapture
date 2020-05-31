package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.args.InOutArgument
import com.mattmik.rapira.errors.IllegalArgumentError
import com.mattmik.rapira.errors.IncorrectArgumentCountError
import com.mattmik.rapira.params.ParamType
import com.mattmik.rapira.params.Parameter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.mockk.mockk

class FunctionTest : WordSpec({

    fun makeFunctionParams(vararg paramNames: String) =
        paramNames.map { paramName ->
            Parameter(
                ParamType.In,
                paramName
            )
        }

    "call" should {
        "throw exception when param and argument count differ" {
            val params = makeFunctionParams("param1", "param2", "param3")
            val arguments = listOf(
                InArgument(mockk()),
                InArgument(mockk())
            )
            val function = Function(null, null, params)
            shouldThrow<IncorrectArgumentCountError> {
                function.call(Environment(), arguments, mockk())
            }
        }

        "throw exception when given in-out arguments" {
            val mockVariableContext = mockk<RapiraLangParser.VariableContext> {
                start = mockk()
                stop = mockk()
            }
            val params = makeFunctionParams("param1")
            val arguments = listOf(
                InOutArgument(mockVariableContext)
            )
            val function = Function(null, null, params)

            shouldThrow<IllegalArgumentError> {
                function.call(Environment(), arguments, mockk())
            }
        }
    }

    "toString" should {
        "return user friendly representation" {
            Function() shouldConvertToString "function"
        }
    }
})
