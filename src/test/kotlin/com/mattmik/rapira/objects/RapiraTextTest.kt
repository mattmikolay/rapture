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

class RapiraTextTest : StringSpec({
    "addition with text returns text" {
        checkAll<String, String> { a, b ->
            a.toRText() + b.toRText() shouldBe RText(a + b)
        }
    }

    "multiplication with zero returns empty string" {
        checkAll<String> {
            str -> str.toRText() * 0.toRInteger() shouldBe "".toRText()
        }
    }

    "multiplication with positive integer returns text" {
        checkAll(Arb.string(), Arb.positiveInts(max = 500)) { str, num ->
            str.toRText() * num.toRInteger() shouldBe str.repeat(num).toRText()
        }
    }

    "multiplication with negative integer throws exception" {
        checkAll(Arb.string(), Arb.negativeInts()) { str, num ->
            shouldThrow<RapiraInvalidOperationError> {
                str.toRText() * num.toRInteger()
            }
        }
    }

    "length returns integer" {
        checkAll<String> { str -> str.toRText().length() shouldBe RInteger(str.length) }
    }

    "element at with integer returns text" {
        "case".toRText().elementAt(RInteger(2)) shouldBe "a".toRText()
        shouldThrow<RapiraIndexOutOfBoundsError> {
            "case".toRText().elementAt(RInteger(0))
        }
        shouldThrow<RapiraIndexOutOfBoundsError> {
            "case".toRText().elementAt(RInteger(5))
        }
    }

    "slice with integers returns text" {
        val text = "Hello, world!".toRText()

        text.slice(null, null) shouldBe text
        text.slice(8.toRInteger(), null) shouldBe "world!".toRText()
        text.slice(4.toRInteger(), 9.toRInteger()) shouldBe "lo, wo".toRText()
        text.slice(null, 5.toRInteger()) shouldBe "Hello".toRText()
    }

    "toString returns user friendly representation" {
        "Hello, world!".toRText() shouldConvertToString "\"Hello, world!\""
        "How about some \"\"double quotes\"\"? Fancy, eh?".toRText() shouldConvertToString "\"How about some \"double quotes\"? Fancy, eh?\""
    }
})
