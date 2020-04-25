package com.mattmik.rapira

import com.mattmik.rapira.objects.RapiraEmpty
import com.mattmik.rapira.objects.RapiraInteger
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class EnvironmentTest : StringSpec({
    "getObject returns empty as default" {
        val environment = Environment()
        environment.getObject("not_present") shouldBe RapiraEmpty
    }

    "objects can be stored and retrieved" {
        val environment = Environment()
        val obj = RapiraInteger(123)
        environment.setObject("abc", obj)
        environment.getObject("abc") shouldBe obj
    }
})
