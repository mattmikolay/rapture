package com.mattmik.rapira.objects

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class RIntegerTest : WordSpec({
    "plus" should {
        "succeed with integer when given integer" {
            checkAll<Int, Int> { a, b ->
                RInteger(a) + RInteger(b) shouldSucceedWith RInteger(a + b)
            }
        }

        "succeed with real when given real" {
            checkAll<Int, Double> { a, b ->
                RInteger(a) + Real(b) shouldSucceedWith Real(a + b)
            }
        }
    }

    "minus" should {
        "succeed with integer when given integer" {
            checkAll<Int, Int> { a, b ->
                RInteger(a) - RInteger(b) shouldSucceedWith RInteger(a - b)
            }
        }

        "succeed with real when given real" {
            checkAll<Int, Double> { a, b ->
                RInteger(a) - Real(b) shouldSucceedWith Real(a - b)
            }
        }
    }

    "negate" should {
        "succeed with integer" {
            checkAll<Int> { num ->
                RInteger(num).negate() shouldSucceedWith RInteger(-num)
            }
        }
    }

    "compareTo" should {
        "compare with integers" {
            checkAll<Int, Int> { a, b ->
                a.toRInteger().compareTo(b.toRInteger()) shouldBe a.compareTo(b)
            }
        }

        "compare with real numbers" {
            checkAll<Int, Double> { a, b ->
                a.toRInteger().compareTo(b.toReal()) shouldBe a.compareTo(b)
            }
        }
    }

    "toString" should {
        "return user friendly representation" {
            checkAll<Int> { num ->
                RInteger(num) shouldConvertToString "$num"
            }
        }
    }
})
