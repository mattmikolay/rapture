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
            a.toRapiraText() + b.toRapiraText() shouldBe RapiraText(a + b)
        }
    }

    "multiplication with zero returns empty string" {
        checkAll<String> {
            str -> str.toRapiraText() * 0.toRapiraInteger() shouldBe "".toRapiraText()
        }
    }

    "multiplication with positive integer returns text" {
        checkAll(Arb.string(), Arb.positiveInts(max = 500)) { str, num ->
            str.toRapiraText() * num.toRapiraInteger() shouldBe str.repeat(num).toRapiraText()
        }
    }

    "multiplication with negative integer throws exception" {
        checkAll(Arb.string(), Arb.negativeInts()) { str, num ->
            shouldThrow<RapiraInvalidOperationError> {
                str.toRapiraText() * num.toRapiraInteger()
            }
        }
    }

    "length returns integer" {
        checkAll<String> { str -> str.toRapiraText().length() shouldBe RapiraInteger(str.length) }
    }

    "element at with integer returns text" {
        "case".toRapiraText().elementAt(RapiraInteger(2)) shouldBe "a".toRapiraText()
        shouldThrow<RapiraIndexOutOfBoundsError> {
            "case".toRapiraText().elementAt(RapiraInteger(0))
        }
        shouldThrow<RapiraIndexOutOfBoundsError> {
            "case".toRapiraText().elementAt(RapiraInteger(5))
        }
    }

    "slice with integers returns text" {
        val text = "Hello, world!".toRapiraText()

        text.slice(null, null) shouldBe text
        text.slice(8.toRapiraInteger(), null) shouldBe "world!".toRapiraText()
        text.slice(4.toRapiraInteger(), 9.toRapiraInteger()) shouldBe "lo, wo".toRapiraText()
        text.slice(null, 5.toRapiraInteger()) shouldBe "Hello".toRapiraText()
    }

    "toString returns user friendly representation" {
        "Hello, world!".toRapiraText() shouldConvertToString "\"Hello, world!\""
        "How about some \"\"double quotes\"\"? Fancy, eh?".toRapiraText() shouldConvertToString "\"How about some \"double quotes\"? Fancy, eh?\""
    }
})
