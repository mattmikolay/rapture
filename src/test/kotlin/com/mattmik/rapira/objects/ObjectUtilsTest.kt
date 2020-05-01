package com.mattmik.rapira.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

class ObjectUtilsTest : StringSpec({

    "parseEscapedText handles double quotes" {
        parseEscapedText("\"\"") shouldBe Text("")
        parseEscapedText("\"Test!\"") shouldBe Text("Test!")
        parseEscapedText("\"How about some \"\"double quotes\"\"?\"") shouldBe Text("How about some \"double quotes\"?")
    }

    "formatRapiraObject returns correct string representations" {
        formatRObject(Empty) shouldBe "empty"
        formatRObject(Procedure()) shouldBe "procedure"
        formatRObject(Function()) shouldBe "function"
        formatRObject(Logical(true)) shouldBe "yes"
        formatRObject(Logical(false)) shouldBe "no"
        formatRObject(RInteger(123)) shouldBe "123"
        formatRObject(Real(1.4)) shouldBe "1.4"
        formatRObject(Text("")) shouldBe ""
        formatRObject(Text("Hello!")) shouldBe "Hello!"
        formatRObject(Text("How about some \"double quotes\"?")) shouldBe "How about some \"double quotes\"?"
    }

    "toRInteger converts int" {
        checkAll<Int> { num ->
            num.toRInteger() shouldBe RInteger(num)
        }
    }

    "toReal converts double" {
        checkAll<Double> { num ->
            num.toReal() shouldBe Real(num)
        }
    }

    "toLogical converts boolean" {
        checkAll<Boolean> { bool ->
            bool.toLogical() shouldBe Logical(bool)
        }
    }

    "toText converts string" {
        checkAll<String> { str ->
            str.toText() shouldBe Text(str)
        }
    }

    "toSequence converts list" {
        checkAll(Arb.list(Arb.int())) { list ->
            val rapiraObjectList = list.map { num -> num.toRInteger() }
            rapiraObjectList.toSequence() shouldBe Sequence(rapiraObjectList)
        }
    }
})
