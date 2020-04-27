package com.mattmik.rapira

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.RapiraEmpty
import com.mattmik.rapira.objects.RapiraInteger
import com.mattmik.rapira.objects.RapiraLogical
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class EnvironmentTest : WordSpec({
    "getObject" should {
        "return empty as default" {
            val environment = Environment()
            environment.getObject("not_present") shouldBe RapiraEmpty
        }

        "return special values" {
            val environment = Environment()
            environment.getObject("empty") shouldBe RapiraEmpty
            environment.getObject("yes") shouldBe RapiraLogical(true)
            environment.getObject("no") shouldBe RapiraLogical(false)
        }
    }

    "setObject" should {
        "store custom objects" {
            val environment = Environment()
            val obj = RapiraInteger(123)
            environment.setObject("abc", obj)
            environment.getObject("abc") shouldBe obj
        }

        "throw exception with reserved names" {
            val environment = Environment()
            listOf(
                "empty",
                "yes",
                "no"
            ).forEach { name ->
                shouldThrow<RapiraInvalidOperationError> {
                    environment.setObject(name, RapiraEmpty)
                }
            }
        }
    }
})
