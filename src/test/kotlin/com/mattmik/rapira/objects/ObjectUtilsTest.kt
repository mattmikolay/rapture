package com.mattmik.rapira.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ObjectUtilsTest : StringSpec({

    "parseEscapedText handles double quotes" {
        parseEscapedText("\"\"") shouldBe RapiraText("")
        parseEscapedText("\"Test!\"") shouldBe RapiraText("Test!")
        parseEscapedText("\"How about some \"\"double quotes\"\"?\"") shouldBe RapiraText("How about some \"double quotes\"?")
    }

    "formatRapiraObject returns correct string representations" {
        formatRapiraObject(RapiraEmpty) shouldBe "empty"
        formatRapiraObject(RapiraProcedure) shouldBe "procedure"
        formatRapiraObject(RapiraFunction()) shouldBe "function"
        formatRapiraObject(RapiraLogical(true)) shouldBe "yes"
        formatRapiraObject(RapiraLogical(false)) shouldBe "no"
        formatRapiraObject(RapiraInteger(123)) shouldBe "123"
        formatRapiraObject(RapiraReal(1.4)) shouldBe "1.4"
        formatRapiraObject(RapiraText("")) shouldBe ""
        formatRapiraObject(RapiraText("Hello!")) shouldBe "Hello!"
        formatRapiraObject(RapiraText("How about some \"double quotes\"?")) shouldBe "How about some \"double quotes\"?"
    }
})
