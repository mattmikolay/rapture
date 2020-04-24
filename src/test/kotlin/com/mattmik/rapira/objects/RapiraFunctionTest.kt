package com.mattmik.rapira.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RapiraFunctionTest : StringSpec({
    "toString returns user friendly representation" {
        RapiraFunction.toString() shouldBe "function"
    }
})
