package com.mattmik.rapira.objects

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.beOfType
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.negativeInts
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class TextTest : WordSpec({
    "plus" should {
        "return text when given text" {
            checkAll<String, String> { a, b ->
                Text(a) + Text(b) shouldBe Text(a + b)
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
                str.toText() * num.toRInteger() should beOfType<OperationResult.Error>()
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
            text.elementAt(RInteger(0)) should beOfType<OperationResult.Error>()
            text.elementAt(RInteger(5)) should beOfType<OperationResult.Error>()
        }
    }

    "slice" should {
        "succeed with text when given integer" {
            val text = Text("Hello, world!")
            text.slice(null, null) shouldSucceedWith text
            text.slice(8.toRInteger(), null) shouldSucceedWith "world!".toText()
            text.slice(4.toRInteger(), 9.toRInteger()) shouldSucceedWith "lo, wo".toText()
            text.slice(null, 5.toRInteger()) shouldSucceedWith "Hello".toText()
        }
    }

    "toString" should {
        "return user friendly representation" {
            Text("Hello, world!") shouldConvertToString "\"Hello, world!\""
            Text("How about some \"\"double quotes\"\"? Fancy, eh?") shouldConvertToString "\"How about some \"double quotes\"? Fancy, eh?\""
        }
    }
})
