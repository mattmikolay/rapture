package com.mattmik.rapira.objects

import com.mattmik.rapira.CONST_YES
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.property.Arb
import io.kotest.property.arbitrary.negativeInts
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class TextTest : WordSpec({
    "plus" should {
        "succeed with text when given text" {
            checkAll<String, String> { a, b ->
                Text(a) + Text(b) shouldSucceedWith Text(a + b)
            }
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(RInteger(1)),
                row(CONST_YES),
                row(Real(1.0)),
                row(Sequence())
            ) { obj ->
                (Text("Hello, world!") + obj).shouldError()
            }
        }
    }

    "times" should {
        "succeed with empty string when given 0" {
            checkAll<String> { str ->
                Text(str) * RInteger(0) shouldSucceedWith Text("")
            }
        }

        "succeed with text when given positive integer" {
            checkAll(Arb.string(), Arb.positiveInts(max = 500)) { str, num ->
                Text(str) * RInteger(num) shouldSucceedWith str.repeat(num).toText()
            }
        }

        "error when given negative integer" {
            checkAll(Arb.string(), Arb.negativeInts()) { str, num ->
                (str.toText() * num.toRInteger()).shouldError()
            }
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(Text("Hello, world!")),
                row(CONST_YES),
                row(Real(1.0)),
                row(Sequence())
            ) { obj ->
                (Text("Hello, world!") * obj).shouldError()
            }
        }
    }

    "length" should {
        "succeed with integer" {
            checkAll<String> { str ->
                Text(str).length() shouldSucceedWith RInteger(str.length)
            }
        }
    }

    "element at" should {
        val text = "case".toText()

        "succeed with text when given valid integer" {
            text.elementAt(RInteger(2)) shouldSucceedWith "a".toText()
        }

        "error when given out of bounds integer" {
            text.elementAt(RInteger(0)).shouldError()
            text.elementAt(RInteger(5)).shouldError()
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(Text("Hello, world!")),
                row(CONST_YES),
                row(Real(1.0)),
                row(Sequence())
            ) { obj ->
                Text("Hello, world!").elementAt(obj).shouldError()
            }
        }
    }

    "slice" should {
        "succeed with text when given integer" {
            val text = Text("Hello, world!")
            text.slice(null, null) shouldSucceedWith text
            text.slice(8.toRInteger(), null) shouldSucceedWith "world!".toText()
            text.slice(4.toRInteger(), 9.toRInteger()) shouldSucceedWith "lo, wo".toText()
            text.slice(null, 5.toRInteger()) shouldSucceedWith "Hello".toText()

            text.slice(1.toRInteger(), 0.toRInteger()) shouldSucceedWith "".toText()
            text.slice(14.toRInteger(), 13.toRInteger()) shouldSucceedWith "".toText()
        }

        "error when start index is out of bounds" {
            val text = Text("Hello, world!")
            text.slice(0.toRInteger(), null).shouldError()
            text.slice((-1).toRInteger(), null).shouldError()
        }

        "error when end index is out of bounds" {
            val text = Text("Hello, world!")
            text.slice(null, 14.toRInteger()).shouldError()
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(Text("Hello, world!")),
                row(CONST_YES),
                row(Real(1.0)),
                row(Sequence())
            ) { obj ->
                Text("Hello, world!").slice(null, obj).shouldError()
                Text("Hello, world!").slice(obj, null).shouldError()
                Text("Hello, world!").slice(obj, obj).shouldError()
            }
        }
    }

    "toString" should {
        "return user friendly representation" {
            Text("Hello, world!") shouldConvertToString "\"Hello, world!\""
            Text("How about some \"\"double quotes\"\"? Fancy, eh?") shouldConvertToString "\"How about some \"double quotes\"? Fancy, eh?\""
        }
    }
})
