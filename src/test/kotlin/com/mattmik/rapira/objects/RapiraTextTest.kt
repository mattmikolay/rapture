package com.mattmik.rapira.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RapiraTextTest : StringSpec({
    "toString returns user friendly representation" {
        RapiraText("Hello, world!").toString() shouldBe "\"Hello, world!\""
        RapiraText("How about some \"\"double quotes\"\"? Fancy, eh?").toString() shouldBe "\"How about some \"double quotes\"? Fancy, eh?\""
    }
})
