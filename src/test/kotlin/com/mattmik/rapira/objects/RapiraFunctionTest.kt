package com.mattmik.rapira.objects

import io.kotest.core.spec.style.StringSpec

class RapiraFunctionTest : StringSpec({
    "toString returns user friendly representation" {
        RapiraFunction shouldConvertToString "function"
    }
})
