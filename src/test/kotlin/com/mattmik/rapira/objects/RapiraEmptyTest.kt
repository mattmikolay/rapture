package com.mattmik.rapira.objects

import io.kotest.core.spec.style.StringSpec

class RapiraEmptyTest : StringSpec({
    "toString returns user friendly representation" {
        RapiraEmpty shouldConvertToString "empty"
    }
})
