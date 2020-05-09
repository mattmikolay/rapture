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

class ProcedureTest : WordSpec({

    fun makeProcedureParams(vararg paramNames: String) =
        paramNames.map { paramName -> Parameter(ParamType.InOut, paramName) }

    "call" should {
        "read extern objects from old environment" {
            val mockEnvironment = mockk<Environment>()
            val extern = listOf(
                "externVariable1",
                "externVariable2",
                "externVariable3"
            )
            every { mockEnvironment[any()] } returns SimpleVariable(Empty)
            val procedure = Procedure(extern = extern)

            procedure.call(mockEnvironment, emptyList())

            verifyAll {
                mockEnvironment["externVariable1"]
                mockEnvironment["externVariable2"]
                mockEnvironment["externVariable3"]
            }
        }

        "throw exception when param and argument count differ" {
            val params = makeProcedureParams("param1", "param2", "param3")
            val arguments = listOf(
                InOutArgument(mockk()),
                InOutArgument(mockk())
            )
            val procedure = Procedure(null, null, params)
            shouldThrow<RapiraInvalidOperationError> {
                procedure.call(Environment(), arguments)
            }
        }

        "throw exception when param and argument type do not match" {
            val params = makeProcedureParams("param1")
            val arguments = listOf(
                InArgument(mockk())
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
