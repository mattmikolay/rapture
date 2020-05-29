package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.args.InOutArgument
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.errors.RapiraIncorrectArgumentCountError
import com.mattmik.rapira.params.ParamType
import com.mattmik.rapira.params.Parameter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.mockk.mockk

class ProcedureTest : WordSpec({

    fun makeProcedureParams(vararg paramNames: String) =
        paramNames.map { paramName ->
            Parameter(
                ParamType.InOut,
                paramName
            )
        }

    "call" should {
        "throw exception when param and argument count differ" {
            val mockVariableContext = mockk<RapiraLangParser.VariableContext> {
                start = mockk()
                stop = mockk()
            }
            val params = makeProcedureParams("param1", "param2", "param3")
            val arguments = listOf(
                InOutArgument(mockVariableContext),
                InOutArgument(mockVariableContext)
            )
            val procedure = Procedure(null, null, params)

            shouldThrow<RapiraIncorrectArgumentCountError> {
                procedure.call(Environment(), arguments)
            }
        }

        "throw exception when param and argument type do not match" {
            val mockExpressionContext = mockk<RapiraLangParser.ExpressionContext> {
                start = mockk()
                stop = mockk()
            }
            val params = makeProcedureParams("param1")
            val arguments = listOf(
                InArgument(mockExpressionContext)
            )
            val procedure = Procedure(null, null, params)

            shouldThrow<RapiraIllegalArgumentException> {
                procedure.call(Environment(), arguments)
            }
        }
    }

    "toString" should {
        "return user friendly representation" {
            Procedure() shouldConvertToString "procedure"
        }
    }
})
