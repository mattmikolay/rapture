package com.mattmik.rapira.objects

import com.mattmik.rapira.CONST_YES
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.negativeInts
import io.kotest.property.arbitrary.positiveDoubles
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.checkAll
import kotlin.math.exp
import kotlin.math.ln

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

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(CONST_YES),
                row(Text("hello")),
                row(Sequence())
            ) { obj ->
                (Real(123.0) + obj).shouldError()
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

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(CONST_YES),
                row(Text("hello")),
                row(Sequence())
            ) { obj ->
                (Real(123.0) - obj).shouldError()
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

    "times" should {
        "succeed with real when given integer" {
            checkAll<Double, Int> { a, b ->
                Real(a) * RInteger(b) shouldSucceedWith Real(a * b)
            }
        }

        "succeed with real when given real" {
            checkAll<Double, Double> { a, b ->
                Real(a) * Real(b) shouldSucceedWith Real(a * b)
            }
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(CONST_YES),
                row(Text("hello")),
                row(Sequence())
            ) { obj ->
                (Real(123.0) * obj).shouldError()
            }
        }
    }

    "div" should {
        "succeed with real when given positive integer" {
            checkAll(Arb.double(), Arb.positiveInts()) { a, b ->
                Real(a) / RInteger(b) shouldSucceedWith Real(a / b)
            }
        }

        "succeed with real when given negative integer" {
            checkAll(Arb.double(), Arb.negativeInts()) { a, b ->
                Real(a) / RInteger(b) shouldSucceedWith Real(a / b)
            }
        }

        "error when given integer zero" {
            checkAll<Double> { num ->
                (num.toReal() / RInteger(0)).shouldError()
            }
        }

        "succeed with real when given positive real" {
            checkAll(Arb.double(), Arb.positiveDoubles()) { a, b ->
                Real(a) / Real(b) shouldSucceedWith Real(a / b)
            }
        }

        "succeed with real when given negative real" {
            checkAll(Arb.double(), Arb.positiveDoubles()) { a, b ->
                Real(a) / Real(-b) shouldSucceedWith Real(a / -b)
            }
        }

        "error when given real zero" {
            checkAll<Double> { num ->
                (num.toReal() / Real(0.0)).shouldError()
                (num.toReal() / Real(-0.0)).shouldError()
            }
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(CONST_YES),
                row(Text("hello")),
                row(Sequence())
            ) { obj ->
                (Real(123.0) / obj).shouldError()
            }
        }
    }

    "power" should {
        "succeed with real when given integer" {
            checkAll<Double, Int> { a, b ->
                Real(a).power(RInteger(b)) shouldSucceedWith exp(ln(a) * b).toReal()
            }
        }

        "succeed with real when given real" {
            checkAll<Double, Double> { a, b ->
                Real(a).power(Real(b)) shouldSucceedWith exp(ln(a) * b).toReal()
            }
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(CONST_YES),
                row(Text("hello")),
                row(Sequence())
            ) { obj ->
                Real(123.0).power(obj).shouldError()
            }
        }
    }

    "compare" should {
        "succeed when given integer" {
            checkAll<Double, Int> { a, b ->
                a.toReal().compare(b.toRInteger()) shouldSucceedWith a.compareTo(b)
            }
        }

        "succeed when given real" {
            checkAll<Double, Double> { a, b ->
                a.toReal().compare(b.toReal()) shouldSucceedWith a.compareTo(b)
            }
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(CONST_YES),
                row(Text("hello")),
                row(Sequence())
            ) { obj ->
                Real(123.0).compare(obj).shouldError()
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
