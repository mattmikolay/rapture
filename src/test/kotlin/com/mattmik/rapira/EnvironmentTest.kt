package com.mattmik.rapira

import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.shouldSucceedWith
import io.kotest.core.spec.style.WordSpec

class EnvironmentTest : WordSpec({
    "get" should {
        "succeed with empty as default" {
            val environment = Environment()
            environment["not_present"].getValue() shouldSucceedWith Empty
        }

        "succeed with English special values" {
            val environment = Environment()
            environment["empty"].getValue() shouldSucceedWith Empty
            environment["yes"].getValue() shouldSucceedWith CONST_YES
            environment["no"].getValue() shouldSucceedWith CONST_NO
            environment["lf"].getValue() shouldSucceedWith CONST_LINE_FEED
            environment["pi"].getValue() shouldSucceedWith CONST_PI
        }

        "succeed with Russian special values" {
            val environment = Environment()
            environment["пусто"].getValue() shouldSucceedWith Empty
            environment["да"].getValue() shouldSucceedWith CONST_YES
            environment["нет"].getValue() shouldSucceedWith CONST_NO
            environment["пс"].getValue() shouldSucceedWith CONST_LINE_FEED
            environment["пи"].getValue() shouldSucceedWith CONST_PI
        }
    }

    "set" should {
        "store custom objects" {
            val environment = Environment()
            val obj = RInteger(123)
            environment["abc"].setValue(obj) shouldSucceedWith Unit
            environment["abc"].setValue(obj) shouldSucceedWith Unit
        }
    }
})
