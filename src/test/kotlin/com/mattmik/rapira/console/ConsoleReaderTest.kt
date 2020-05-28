package com.mattmik.rapira.console

import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.LogicalNo
import com.mattmik.rapira.objects.LogicalYes
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.Real
import com.mattmik.rapira.objects.Text
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class ConsoleReaderTest : WordSpec({

    "parseObject" should {
        "handle empty" {
            ConsoleReader.parseObject("empty") shouldBe Empty
        }

        "handle yes" {
            ConsoleReader.parseObject("yes") shouldBe LogicalYes
        }

        "handle no" {
            ConsoleReader.parseObject("no") shouldBe LogicalNo
        }

        "handle text" {
            ConsoleReader.parseObject("\"\"") shouldBe
                Text("")
            ConsoleReader.parseObject("\"Test!\"") shouldBe
                Text("Test!")
            ConsoleReader.parseObject("\"How about some \"\"double quotes\"\"?\"") shouldBe
                Text("How about some \"double quotes\"?")
        }

        "handle integers" {
            ConsoleReader.parseObject("123") shouldBe RInteger(123)
            ConsoleReader.parseObject("-123") shouldBe RInteger(-123)
            ConsoleReader.parseObject("+123") shouldBe RInteger(123)
        }

        "handle real numbers" {
            forAll(
                row("1.23", 1.23),
                row("1.23", 1.23),
                row("-1.23", -1.23),
                row("+1.23", 1.23),
                row("1e3", 1000.0),
                row("-1e3", -1000.0),
                row("+1e3", 1000.0),
                row("1e-3", 0.001),
                row("-1e-3", -0.001),
                row("+1e-3", 0.001),
                row("1.23e3", 1230.0),
                row("-1.23e3", -1230.0),
                row("+1.23e3", 1230.0),
                row("1.23e-3", 0.00123),
                row("-1.23e-3", -0.00123),
                row("+1.23e-3", 0.00123)
            ) { str, num ->
                ConsoleReader.parseObject(str) shouldBe Real(num)
            }
        }
    }
})
