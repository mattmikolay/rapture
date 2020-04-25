package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraIndexOutOfBoundsError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe

class RapiraTextTest : StringSpec({
    "addition with text returns text" {
        forAll<String, String> { a, b ->
            RapiraText(a).add(RapiraText(b)) == RapiraText(a + b)
        }
    }

    "multiplication with integer returns text" {
        forAll<String, Int> { a, b ->
            RapiraText(a).add(RapiraInteger(b)) == RapiraText(a.repeat(b))
        }
    }

    "length returns integer" {
        forAll<String> { str -> RapiraText(str).length() == RapiraInteger(str.length) }
    }

    "element at with integer returns text" {
        RapiraText("case").elementAt(RapiraInteger(2)) shouldBe RapiraText("a")
        shouldThrow<RapiraIndexOutOfBoundsError> {
            RapiraText("case").elementAt(RapiraInteger(0))
        }
        shouldThrow<RapiraIndexOutOfBoundsError> {
            RapiraText("case").elementAt(RapiraInteger(5))
        }
    }

    "toString returns user friendly representation" {
        RapiraText("Hello, world!") shouldConvertToString "\"Hello, world!\""
        RapiraText("How about some \"\"double quotes\"\"? Fancy, eh?") shouldConvertToString "\"How about some \"double quotes\"? Fancy, eh?\""
    }
})
