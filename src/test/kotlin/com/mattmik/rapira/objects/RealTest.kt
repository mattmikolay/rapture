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
        "succeed with real when given integer" {
            checkAll<Double, Int> { a, b ->
                Real(a) - RInteger(b) shouldSucceedWith Real(a - b)
            }
        }

        "succeed with real when given real" {
            checkAll<Double, Double> { a, b ->
                Real(a) - Real(b) shouldSucceedWith Real(a - b)
            }
        }
    }

    "negate" should {
        "succeed with real" {
            checkAll<Double> { num ->
                Real(num).negate() shouldSucceedWith Real(-num)
            }
        }
    }

    "compareTo" should {
        "compare with integers" {
            checkAll<Double, Int> { a, b ->
                a.toReal().compareTo(b.toRInteger()) shouldBe a.compareTo(b)
            }
        }

        "compare with real numbers" {
            checkAll<Double, Double> { a, b ->
                a.toReal().compareTo(b.toReal()) shouldBe a.compareTo(b)
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
