package com.mattmik.rapira

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.RapiraEmpty
import com.mattmik.rapira.objects.RapiraInteger
import com.mattmik.rapira.objects.RapiraLogical
import com.mattmik.rapira.objects.toRapiraReal
import com.mattmik.rapira.objects.toRapiraText
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import kotlin.math.PI

class EnvironmentTest : WordSpec({
    "get" should {
        "return empty as default" {
            val environment = Environment()
            environment["not_present"] shouldBe RapiraEmpty
        }

        "return special values" {
            val environment = Environment()
            environment["empty"] shouldBe RapiraEmpty
            environment["yes"] shouldBe RapiraLogical(true)
            environment["no"] shouldBe RapiraLogical(false)
            environment["lf"] shouldBe System.lineSeparator().toRapiraText()
            environment["pi"] shouldBe PI.toRapiraReal()
        }
    }

    "set" should {
        "store custom objects" {
            val environment = Environment()
            val obj = RapiraInteger(123)
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
                    environment.set(name, RapiraEmpty)
                }
            }
        }
    }
})
