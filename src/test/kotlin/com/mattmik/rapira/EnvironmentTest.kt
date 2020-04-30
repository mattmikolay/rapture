package com.mattmik.rapira

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.REmpty
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RLogical
import com.mattmik.rapira.objects.toRReal
import com.mattmik.rapira.objects.toRText
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import kotlin.math.PI

class EnvironmentTest : WordSpec({
    "get" should {
        "return empty as default" {
            val environment = Environment()
            environment["not_present"] shouldBe REmpty
        }

        "return special values" {
            val environment = Environment()
            environment["empty"] shouldBe REmpty
            environment["yes"] shouldBe RLogical(true)
            environment["no"] shouldBe RLogical(false)
            environment["lf"] shouldBe System.lineSeparator().toRText()
            environment["pi"] shouldBe PI.toRReal()
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
                    environment.set(name, REmpty)
                }
            }
        }
    }
})
