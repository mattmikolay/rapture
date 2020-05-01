package com.mattmik.rapira.objects

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class RealTest : WordSpec({
    "plus" should {
        "return real when given integer" {
            checkAll<Double, Int> { a, b ->
                Real(a) + RInteger(b) shouldBe Real(a + b)
            }
        }

        "return real when given real" {
            checkAll<Double, Double> { a, b ->
                Real(a) + Real(b) shouldBe Real(a + b)
            }
        }
    }

    "minus" should {
        "return real when given integer" {
            checkAll<Double, Int> { a, b ->
                Real(a) - RInteger(b) shouldBe Real(a - b)
            }
        }

        "return real when given real" {
            checkAll<Double, Double> { a, b ->
                Real(a) - Real(b) shouldBe Real(a - b)
            }
        }
    }

    "negate" should {
        "return real" {
            checkAll<Double> { num ->
                Real(num).negate() shouldBe Real(-num)
            }
        }
    }

    "toString" should {
        "return user friendly representation" {
            checkAll<Double> { num ->
                Real(num) shouldConvertToString "$num"
            }
        }
    }
})
