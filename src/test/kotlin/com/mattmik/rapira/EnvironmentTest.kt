package com.mattmik.rapira

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.Logical
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.toReal
import com.mattmik.rapira.objects.toText
import com.mattmik.rapira.variables.SimpleVariable
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import kotlin.math.PI

class EnvironmentTest : WordSpec({
    "get" should {
        "return empty as default" {
            val environment = Environment()
            environment["not_present"].value shouldBe Empty
        }

        "return special values" {
            val environment = Environment()
            environment["empty"].value shouldBe Empty
            environment["yes"].value shouldBe Logical(true)
            environment["no"].value shouldBe Logical(false)
            environment["lf"].value shouldBe System.lineSeparator().toText()
            environment["pi"].value shouldBe PI.toReal()
        }
    }

    "set" should {
        "store custom objects" {
            val environment = Environment()
            val obj = RInteger(123)
            environment["abc"].value = obj
            environment["abc"].value shouldBe obj
        }

        "throw exception with reserved names" {
            val environment = Environment()
            listOf(
                "empty",
                "yes",
                "no"
            ).forEach { name ->
                shouldThrowUnit<RapiraInvalidOperationError> {
                    environment[name] = SimpleVariable(Empty)
                }
            }
        }
    }
})
