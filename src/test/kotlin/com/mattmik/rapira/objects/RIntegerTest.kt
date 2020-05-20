package com.mattmik.rapira.objects

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class RIntegerTest : WordSpec({
    "plus" should {
        "return integer when given integer" {
            checkAll<Int, Int> { a, b ->
                RInteger(a) + RInteger(b) shouldBe RInteger(a + b)
            }
        }

        "return real when given real" {
            checkAll<Int, Double> { a, b ->
                RInteger(a) + Real(b) shouldBe Real(a + b)
            }
        }
    }

    "minus" should {
        "return integer when given integer" {
            checkAll<Int, Int> { a, b ->
                RInteger(a) - RInteger(b) shouldBe RInteger(a - b)
            }
        }

        "return real when given real" {
            checkAll<Int, Double> { a, b ->
                RInteger(a) - Real(b) shouldBe Real(a - b)
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
