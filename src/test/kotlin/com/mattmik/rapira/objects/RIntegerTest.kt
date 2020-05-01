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

    "less than" should {
        "return logical when given integer" {
            checkAll<Int, Int> { a, b ->
                RInteger(a) lessThan RInteger(b) shouldBe Logical(a < b)
            }
        }

        "return logical when given real" {
            checkAll<Int, Double> { a, b ->
                a.toRInteger() lessThan b.toReal() shouldBe Logical(a < b)
            }
        }
    }

    "less than or equal to" should {
        "return logical when given integer" {
            checkAll<Int, Int> { a, b ->
                a.toRInteger() lessThanEqualTo b.toRInteger() shouldBe Logical(a <= b)
            }
        }

        "return logical when given real" {
            checkAll<Int, Double> { a, b ->
                a.toRInteger() lessThanEqualTo b.toReal() shouldBe Logical(a <= b)
            }
        }
    }

    "greater than" should {
        "return logical when given integer" {
            checkAll<Int, Int> { a, b ->
                a.toRInteger() greaterThan b.toRInteger() shouldBe Logical(a > b)
            }
        }

        "return logical when given real" {
            checkAll<Int, Double> { a, b ->
                a.toRInteger() greaterThan b.toReal() shouldBe Logical(a > b)
            }
        }
    }

    "greater than or equal to" should {
        "return logical when given integer" {
            checkAll<Int, Int> { a, b ->
                a.toRInteger() greaterThanEqualTo b.toRInteger() shouldBe Logical(a >= b)
            }
        }

        "return logical when given real" {
            checkAll<Int, Double> { a, b ->
                a.toRInteger() greaterThanEqualTo b.toReal() shouldBe Logical(a >= b)
            }
        }
    }

    "negate" should {
        "return integer" {
            checkAll<Int> { num ->
                RInteger(num).negate() shouldBe RInteger(-num)
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
