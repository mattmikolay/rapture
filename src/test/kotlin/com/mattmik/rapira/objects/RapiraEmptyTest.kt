package com.mattmik.rapira.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RapiraEmptyTest : StringSpec({
    "toString returns user friendly representation" {
        RapiraEmpty.toString() shouldBe "empty"
    }
})
