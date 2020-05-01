package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraIndexOutOfBoundsError
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.negativeInts
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class TextTest : StringSpec({
    "addition with text returns text" {
        checkAll<String, String> { a, b ->
            a.toText() + b.toText() shouldBe Text(a + b)
        }
    }

    "multiplication with zero returns empty string" {
        checkAll<String> {
            str -> str.toText() * 0.toRInteger() shouldBe "".toText()
        }
    }

    "multiplication with positive integer returns text" {
        checkAll(Arb.string(), Arb.positiveInts(max = 500)) { str, num ->
            str.toText() * num.toRInteger() shouldBe str.repeat(num).toText()
        }
    }

    "multiplication with negative integer throws exception" {
        checkAll(Arb.string(), Arb.negativeInts()) { str, num ->
            shouldThrow<RapiraInvalidOperationError> {
                str.toText() * num.toRInteger()
            }
        }
    }

    "length returns integer" {
        checkAll<String> { str -> str.toText().length() shouldBe RInteger(str.length) }
    }

    "element at with integer returns text" {
        "case".toText().elementAt(RInteger(2)) shouldBe "a".toText()
        shouldThrow<RapiraIndexOutOfBoundsError> {
            "case".toText().elementAt(RInteger(0))
        }
        shouldThrow<RapiraIndexOutOfBoundsError> {
            "case".toText().elementAt(RInteger(5))
        }
    }

    "slice with integers returns text" {
        val text = "Hello, world!".toText()

        text.slice(null, null) shouldBe text
        text.slice(8.toRInteger(), null) shouldBe "world!".toText()
        text.slice(4.toRInteger(), 9.toRInteger()) shouldBe "lo, wo".toText()
        text.slice(null, 5.toRInteger()) shouldBe "Hello".toText()
    }

    "toString returns user friendly representation" {
        "Hello, world!".toText() shouldConvertToString "\"Hello, world!\""
        "How about some \"\"double quotes\"\"? Fancy, eh?".toText() shouldConvertToString "\"How about some \"double quotes\"? Fancy, eh?\""
    }
})
