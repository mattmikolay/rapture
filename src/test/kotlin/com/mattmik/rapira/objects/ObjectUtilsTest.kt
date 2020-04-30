package com.mattmik.rapira.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

class ObjectUtilsTest : StringSpec({

    "parseEscapedText handles double quotes" {
        parseEscapedText("\"\"") shouldBe RText("")
        parseEscapedText("\"Test!\"") shouldBe RText("Test!")
        parseEscapedText("\"How about some \"\"double quotes\"\"?\"") shouldBe RText("How about some \"double quotes\"?")
    }

    "formatRapiraObject returns correct string representations" {
        formatRObject(REmpty) shouldBe "empty"
        formatRObject(RProcedure()) shouldBe "procedure"
        formatRObject(RFunction()) shouldBe "function"
        formatRObject(RLogical(true)) shouldBe "yes"
        formatRObject(RLogical(false)) shouldBe "no"
        formatRObject(RInteger(123)) shouldBe "123"
        formatRObject(RReal(1.4)) shouldBe "1.4"
        formatRObject(RText("")) shouldBe ""
        formatRObject(RText("Hello!")) shouldBe "Hello!"
        formatRObject(RText("How about some \"double quotes\"?")) shouldBe "How about some \"double quotes\"?"
    }

    "toRapiraInteger converts int" {
        checkAll<Int> { num ->
            num.toRInteger() shouldBe RInteger(num)
        }
    }

    "toRapiraReal converts double" {
        checkAll<Double> { num ->
            num.toRReal() shouldBe RReal(num)
        }
    }

    "toRapiraText converts string" {
        checkAll<String> { str ->
            str.toRText() shouldBe RText(str)
        }
    }

    "toRapiraSequence converts list" {
        checkAll(Arb.list(Arb.int())) { list ->
            val rapiraObjectList = list.map { num -> num.toRInteger() }
            rapiraObjectList.toRSequence() shouldBe RSequence(rapiraObjectList)
        }
    }
})
