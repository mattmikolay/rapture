package com.mattmik.rapira

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.Logical
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.toReal
import com.mattmik.rapira.objects.toText
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import kotlin.math.PI

class EnvironmentTest : WordSpec({
    "get" should {
        "return empty as default" {
            val environment = Environment()
            environment["not_present"] shouldBe Empty
        }

        "return special values" {
            val environment = Environment()
            environment["empty"] shouldBe Empty
            environment["yes"] shouldBe Logical(true)
            environment["no"] shouldBe Logical(false)
            environment["lf"] shouldBe System.lineSeparator().toText()
            environment["pi"] shouldBe PI.toReal()
        }
    }

    "set" should {
        "store custom objects" {
            val environment = Environment()
            val obj = RInteger(123)
            environment["abc"] = obj
            environment["abc"] shouldBe obj
        }

        "throw exception with reserved names" {
            val environment = Environment()
            listOf(
                "empty",
                "yes",
                "no"
            ).forEach { name ->
                shouldThrow<RapiraInvalidOperationError> {
                    environment.set(name, Empty)
                }
            }
        }
    }
})
