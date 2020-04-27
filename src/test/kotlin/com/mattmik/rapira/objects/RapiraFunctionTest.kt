package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec

class RapiraFunctionTest : WordSpec({
    "call" should {
        "throw exception when param and argument count differ" {
            val params = listOf("param1", "param2", "param3")
            val arguments = listOf(
                "arg1".toRapiraText(),
                "arg2".toRapiraText()
            )
            val function = RapiraFunction(null, params)
            shouldThrow<RapiraInvalidOperationError> {
                function.call(Environment(), arguments)
            }
        }
    }

    "toString" should {
        "return user friendly representation" {
            RapiraFunction() shouldConvertToString "function"
        }
    }
})
