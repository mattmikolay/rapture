package com.mattmik.rapira.console

import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.Function
import com.mattmik.rapira.objects.Logical
import com.mattmik.rapira.objects.Procedure
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.Real
import com.mattmik.rapira.objects.Text
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class ConsoleWriterTest : WordSpec({
    "formatObject" should {
        "return correct string representations" {
            ConsoleWriter.formatObject(Empty) shouldBe "empty"
            ConsoleWriter.formatObject(Procedure()) shouldBe "proc"
            ConsoleWriter.formatObject(Procedure("foo")) shouldBe "proc[\"foo\"]"
            ConsoleWriter.formatObject(Function()) shouldBe "fun"
            ConsoleWriter.formatObject(Function("foo")) shouldBe "fun[\"foo\"]"
            ConsoleWriter.formatObject(Logical(true)) shouldBe "yes"
            ConsoleWriter.formatObject(Logical(false)) shouldBe "no"
            ConsoleWriter.formatObject(RInteger(123)) shouldBe "123"
            ConsoleWriter.formatObject(Real(1.4)) shouldBe "1.4"
            ConsoleWriter.formatObject(Text("")) shouldBe ""
            ConsoleWriter.formatObject(Text("Hello!")) shouldBe "Hello!"
            ConsoleWriter.formatObject(Text("How about some \"double quotes\"?")) shouldBe "How about some \"double quotes\"?"
        }
    }
})
