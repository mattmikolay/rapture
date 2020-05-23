package com.mattmik.rapira.objects

import io.kotest.core.spec.style.WordSpec
import io.kotest.property.checkAll

class RealTest : WordSpec({
    "plus" should {
        "succeed with real when given integer" {
            checkAll<Double, Int> { a, b ->
                Real(a) + RInteger(b) shouldSucceedWith Real(a + b)
            }
        }

        "succeed with real when given real" {
            checkAll<Double, Double> { a, b ->
                Real(a) + Real(b) shouldSucceedWith Real(a + b)
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

    "compare" should {
        "succeed when given integer" {
            checkAll<Double, Int> { a, b ->
                a.toReal().compare(b.toRInteger()) shouldSucceedWith a.compareTo(b)
            }
        }

        "succeed when given real number" {
            checkAll<Double, Double> { a, b ->
                a.toReal().compare(b.toReal()) shouldSucceedWith a.compareTo(b)
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
