package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec

class RapiraProcedureTest : WordSpec({
    "call" should {
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
