package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll

class RapiraProcedureTest : WordSpec({
    "call" should {
        "read extern objects from old environment" {
            val mockEnvironment = mockk<Environment>()
            val extern = listOf(
                "externVariable1",
                "externVariable2",
                "externVariable3"
            )
            every { mockEnvironment.getObject(any()) } returns RapiraEmpty
            val procedure = RapiraProcedure(extern = extern)

            procedure.call(mockEnvironment, emptyList())

            verifyAll {
                mockEnvironment.getObject("externVariable1")
                mockEnvironment.getObject("externVariable2")
                mockEnvironment.getObject("externVariable3")
            }
        }

        "throw exception when param and argument count differ" {
            val params = listOf("param1", "param2", "param3")
            val arguments = listOf(
                "arg1".toRapiraText(),
                "arg2".toRapiraText()
            )
            val procedure = RapiraProcedure(null, params)
            shouldThrow<RapiraInvalidOperationError> {
                procedure.call(Environment(), arguments)
            }
        }
    }

    "toString" should {
        "return user friendly representation" {
            RapiraProcedure() shouldConvertToString "procedure"
        }
    }
})
