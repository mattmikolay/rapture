package com.mattmik.rapira.objects

import com.mattmik.rapira.CONST_YES
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.negativeInts
import io.kotest.property.arbitrary.positiveDoubles
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

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

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(CONST_YES),
                row(Text("hello")),
                row(Sequence())
            ) { obj ->
                (RInteger(123) + obj).shouldError()
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

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(CONST_YES),
                row(Text("hello")),
                row(Sequence())
            ) { obj ->
                (RInteger(123) - obj).shouldError()
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

    "times" should {
        "succeed with integer when given integer" {
            checkAll<Int, Int> { a, b ->
                RInteger(a) * RInteger(b) shouldSucceedWith RInteger(a * b)
            }
        }

        "succeed with real when given real" {
            checkAll<Int, Double> { a, b ->
                RInteger(a) * Real(b) shouldSucceedWith Real(a * b)
            }
        }

        "succeed with text when given text and non-negative int" {
            checkAll<String> { str ->
                RInteger(0) * Text(str) shouldSucceedWith Text("")
            }
            checkAll(Arb.positiveInts(max = 100), Arb.string()) { num, str ->
                RInteger(num) * Text(str) shouldSucceedWith str.repeat(num).toText()
            }
        }

        "error when given text and negative int" {
            checkAll(Arb.negativeInts(), Arb.string()) { num, str ->
                (RInteger(num) * Text(str)).shouldError()
            }
        }

        "succeed with empty sequence when given sequence and zero" {
            val sequence = listOf(
                1.toRInteger(),
                "Hello, world!".toText(),
                2.toRInteger(),
                "This is a test.".toText()
            ).toSequence()

            RInteger(0) * sequence shouldSucceedWith emptyList<RObject>().toSequence()
        }

        "succeed with sequence when given sequence and positive int" {
            val objList = listOf(
                1.toRInteger(),
                "Hello, world!".toText(),
                2.toRInteger(),
                "This is a test.".toText()
            )

            val expectedList = mutableListOf<RObject>()
            repeat(3) { expectedList.addAll(objList) }
            RInteger(3) * objList.toSequence() shouldSucceedWith expectedList.toSequence()
        }

        "error when given sequence and negative int" {
            val sequence = listOf(
                1.toRInteger(),
                "Hello, world!".toText(),
                2.toRInteger(),
                "This is a test.".toText()
            ).toSequence()

            checkAll(Arb.negativeInts()) { num ->
                (RInteger(num) * sequence).shouldError()
            }
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(CONST_YES)
            ) { obj ->
                (RInteger(123) * obj).shouldError()
            }
        }
    }

    "div" should {
        "succeed when given positive integer" {
            checkAll(Arb.int(), Arb.positiveInts()) { a, b ->
                RInteger(a) / RInteger(b) shouldSucceedWith
                    if (a % b == 0) RInteger(a / b) else Real(a.toDouble() / b)
            }
        }

        "succeed when given negative integer" {
            checkAll(Arb.int(), Arb.negativeInts()) { a, b ->
                RInteger(a) / RInteger(b) shouldSucceedWith
                    if (a % b == 0) RInteger(a / b) else Real(a.toDouble() / b)
            }
        }

        "error when given integer zero" {
            checkAll<Int> { num ->
                (RInteger(num) / RInteger(0)).shouldError()
            }
        }

        "succeed with real when given positive real" {
            checkAll(Arb.int(), Arb.positiveDoubles()) { a, b ->
                RInteger(a) / Real(b) shouldSucceedWith Real(a / b)
            }
        }

        "succeed with real when given negative real" {
            checkAll(Arb.int(), Arb.positiveDoubles()) { a, b ->
                RInteger(a) / Real(-b) shouldSucceedWith Real(a / -b)
            }
        }

        "error when given real zero" {
            checkAll<Int> { num ->
                (RInteger(num) / Real(0.0)).shouldError()
                (RInteger(num) / Real(-0.0)).shouldError()
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
                (RInteger(123) / obj).shouldError()
            }
        }
    }

    "intDivide" should {
        "error with zero" {
            checkAll<Int> { num ->
                RInteger(num).intDivide(0.toRInteger()).shouldError()
            }
        }

        "error with negative integer" {
            checkAll(Arb.int(), Arb.negativeInts()) { a, b ->
                a.toRInteger().intDivide(b.toRInteger()).shouldError()
            }
        }

        "succeed with integer when given positive integer" {
            checkAll(Arb.int(), Arb.positiveInts()) { a, b ->
                a.toRInteger().intDivide(b.toRInteger()) shouldSucceedWith RInteger(a / b)
            }
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Real(123.0)),
                row(Procedure()),
                row(Function()),
                row(CONST_YES),
                row(Text("hello")),
                row(Sequence())
            ) { obj ->
                RInteger(123).intDivide(obj).shouldError()
            }
        }
    }

    "rem" should {
        "error with zero" {
            checkAll<Int> { num ->
                (RInteger(num) % RInteger(0)).shouldError()
            }
        }

        "error with negative integer" {
            checkAll(Arb.int(), Arb.negativeInts()) { a, b ->
                (RInteger(a) % RInteger(b)).shouldError()
            }
        }

        "succeed with integer when given positive integer" {
            checkAll(Arb.int(), Arb.positiveInts()) { a, b ->
                RInteger(a) % RInteger(b) shouldSucceedWith RInteger(a % b)
            }
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Real(123.0)),
                row(Procedure()),
                row(Function()),
                row(CONST_YES),
                row(Text("hello")),
                row(Sequence())
            ) { obj ->
                (RInteger(123) % obj).shouldError()
            }
        }
    }

    "power" should {
        "succeed with integer when given integer" {
            checkAll<Int, Int> { a, b ->
                a.toRInteger().power(b.toRInteger()) shouldSucceedWith a.toDouble().pow(b).toInt().toRInteger()
            }
        }

        "succeed with real when given real" {
            checkAll<Int, Double> { a, b ->
                a.toRInteger().power(b.toReal()) shouldSucceedWith exp(ln(a.toDouble()) * b).toReal()
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
                RInteger(123).power(obj).shouldError()
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

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(CONST_YES),
                row(Text("hello")),
                row(Sequence())
            ) { obj ->
                RInteger(123).compare(obj).shouldError()
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
