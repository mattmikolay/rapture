package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.args.InOutArgument
import com.mattmik.rapira.errors.IllegalArgumentError
import com.mattmik.rapira.params.ParamType
import com.mattmik.rapira.params.Parameter
import com.mattmik.rapira.variables.SimpleVariable
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll

class SubroutineTest : WordSpec({

    "call" should {

        fun makeSubroutine(params: List<Parameter>, extern: List<String>) =
            object : Subroutine(
                name = "TestSubroutine",
                statements = null,
                params = params,
                extern = extern
            ) {
                override val typeName: String
                    get() = "TestSubroutine"
            }

        "read extern objects from old environment" {
            val mockEnvironment = mockk<Environment>()
            val extern = listOf(
                "externVariable1",
                "externVariable2",
                "externVariable3"
            )
            every { mockEnvironment[any()] } returns SimpleVariable(Empty)
            val subroutine = makeSubroutine(
                params = emptyList(),
                extern = extern
            )

            subroutine.call(mockEnvironment, emptyList(), mockk())

            verifyAll {
                mockEnvironment["externVariable1"]
                mockEnvironment["externVariable2"]
                mockEnvironment["externVariable3"]
            }
        }

        "throw IllegalArgumentError when unexpected in arg is encountered" {
            val argument = InArgument(mockk { start = mockk() })
            val param = Parameter(ParamType.InOut, name = "testParam")
            val mockEnvironment = mockk<Environment>(relaxed = true)
            val subroutine = makeSubroutine(
                params = listOf(param),
                extern = emptyList()
            )

            shouldThrow<IllegalArgumentError> {
                subroutine.call(mockEnvironment, arguments = listOf(argument), callToken = mockk())
            }
        }

        "throw IllegalArgumentError when unexpected in-out arg is encountered" {
            val argument = InOutArgument(mockk { start = mockk() })
            val param = Parameter(ParamType.In, name = "testParam")
            val mockEnvironment = mockk<Environment>(relaxed = true)
            val subroutine = makeSubroutine(
                params = listOf(param),
                extern = emptyList()
            )

            shouldThrow<IllegalArgumentError> {
                subroutine.call(mockEnvironment, arguments = listOf(argument), callToken = mockk())
            }
        }
    }
})
