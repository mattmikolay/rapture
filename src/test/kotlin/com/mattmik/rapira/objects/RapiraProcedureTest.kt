package com.mattmik.rapira.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RapiraProcedureTest : StringSpec({
    "toString returns user friendly representation" {
        RapiraProcedure.toString() shouldBe "procedure"
    }
})
