package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraIndexOutOfBoundsError
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
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
        "return empty string when given 0" {
            checkAll<String> { str ->
                Text(str) * RInteger(0) shouldBe Text("")
            }
        }

        "return text when given positive integer" {
            checkAll(Arb.string(), Arb.positiveInts(max = 500)) { str, num ->
                Text(str) * RInteger(num) shouldBe str.repeat(num).toText()
            }
        }

        "throw exception when given negative integer" {
            checkAll(Arb.string(), Arb.negativeInts()) { str, num ->
                shouldThrow<RapiraInvalidOperationError> {
                    str.toText() * num.toRInteger()
                }
            }
        }
    }

    "length" should {
        "return integer" {
            checkAll<String> { str ->
                Text(str).length() shouldBe RInteger(str.length)
            }
        }
    }

    "element at" should {
        "return text when given integer" {
            "case".toText().elementAt(RInteger(2)) shouldBe "a".toText()
            shouldThrow<RapiraIndexOutOfBoundsError> {
                "case".toText().elementAt(RInteger(0))
            }
            shouldThrow<RapiraIndexOutOfBoundsError> {
                "case".toText().elementAt(RInteger(5))
            }
        }
    }

    "slice" should {
        "return text when given integer" {
            val text = Text("Hello, world!")
            text.slice(null, null) shouldBe text
            text.slice(8.toRInteger(), null) shouldBe "world!".toText()
            text.slice(4.toRInteger(), 9.toRInteger()) shouldBe "lo, wo".toText()
            text.slice(null, 5.toRInteger()) shouldBe "Hello".toText()
        }
    }

    "toString" should {
        "return user friendly representation" {
            Text("Hello, world!") shouldConvertToString "\"Hello, world!\""
            Text("How about some \"\"double quotes\"\"? Fancy, eh?") shouldConvertToString "\"How about some \"double quotes\"? Fancy, eh?\""
        }
    }
})
