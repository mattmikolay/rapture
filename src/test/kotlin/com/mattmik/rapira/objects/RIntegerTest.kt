package com.mattmik.rapira.objects

import io.kotest.core.spec.style.WordSpec
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

    "compare" should {
        "compare when given integer" {
            checkAll<Int, Int> { a, b ->
                a.toRInteger().compare(b.toRInteger()) shouldSucceedWith a.compareTo(b)
            }
        }

        "compare when given real number" {
            checkAll<Int, Double> { a, b ->
                a.toRInteger().compare(b.toReal()) shouldSucceedWith a.compareTo(b)
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
