package com.mattmik.rapira.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll

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

    "toString returns user friendly representation" {
        RapiraText("Hello, world!") shouldConvertToString "\"Hello, world!\""
        RapiraText("How about some \"\"double quotes\"\"? Fancy, eh?") shouldConvertToString "\"How about some \"double quotes\"? Fancy, eh?\""
    }
})
