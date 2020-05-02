package com.mattmik.rapira.variables

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.Text
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class ReadOnlyVariableTest : WordSpec({
    "get" should {
        "return wrapped object" {
            val obj = Text("Hello, world!")
            ReadOnlyVariable(obj).value shouldBe obj
        }
    }

    "set" should {
        "throw exception" {
            val obj = Text("Hello, world!")
            val variable = ReadOnlyVariable(obj)
            shouldThrowUnit<RapiraInvalidOperationError> {
                variable.value = Text("TEST")
            }
        }
    }
})
