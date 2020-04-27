package com.mattmik.rapira.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

class ObjectUtilsTest : StringSpec({

    "parseEscapedText handles double quotes" {
        parseEscapedText("\"\"") shouldBe RapiraText("")
        parseEscapedText("\"Test!\"") shouldBe RapiraText("Test!")
        parseEscapedText("\"How about some \"\"double quotes\"\"?\"") shouldBe RapiraText("How about some \"double quotes\"?")
    }

    "formatRapiraObject returns correct string representations" {
        formatRapiraObject(RapiraEmpty) shouldBe "empty"
        formatRapiraObject(RapiraProcedure()) shouldBe "procedure"
        formatRapiraObject(RapiraFunction()) shouldBe "function"
        formatRapiraObject(RapiraLogical(true)) shouldBe "yes"
        formatRapiraObject(RapiraLogical(false)) shouldBe "no"
        formatRapiraObject(RapiraInteger(123)) shouldBe "123"
        formatRapiraObject(RapiraReal(1.4)) shouldBe "1.4"
        formatRapiraObject(RapiraText("")) shouldBe ""
        formatRapiraObject(RapiraText("Hello!")) shouldBe "Hello!"
        formatRapiraObject(RapiraText("How about some \"double quotes\"?")) shouldBe "How about some \"double quotes\"?"
    }

    "toRapiraInteger converts int" {
        checkAll<Int> { num ->
            num.toRapiraInteger() shouldBe RapiraInteger(num)
        }
    }

    "toRapiraReal converts double" {
        checkAll<Double> { num ->
            num.toRapiraReal() shouldBe RapiraReal(num)
        }
    }

    "toRapiraText converts string" {
        checkAll<String> { str ->
            str.toRapiraText() shouldBe RapiraText(str)
        }
    }

    "toRapiraSequence converts list" {
        checkAll(Arb.list(Arb.int())) { list ->
            val rapiraObjectList = list.map { num -> num.toRapiraInteger() }
            rapiraObjectList.toRapiraSequence() shouldBe RapiraSequence(rapiraObjectList)
        }
    }
})
