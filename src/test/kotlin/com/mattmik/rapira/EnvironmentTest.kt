package com.mattmik.rapira

import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.Logical
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.shouldSucceedWith
import com.mattmik.rapira.objects.toReal
import com.mattmik.rapira.objects.toText
import io.kotest.core.spec.style.WordSpec
import kotlin.math.PI

class EnvironmentTest : WordSpec({
    "get" should {
        "succeed with empty as default" {
            val environment = Environment()
            environment["not_present"].getValue() shouldSucceedWith Empty
        }

        "succeed with special values" {
            val environment = Environment()
            environment["empty"].getValue() shouldSucceedWith Empty
            environment["yes"].getValue() shouldSucceedWith Logical(true)
            environment["no"].getValue() shouldSucceedWith Logical(false)
            environment["lf"].getValue() shouldSucceedWith System.lineSeparator().toText()
            environment["pi"].getValue() shouldSucceedWith PI.toReal()
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
