package com.mattmik.rapira.objects

import io.kotest.core.spec.style.StringSpec

class REmptyTest : StringSpec({
    "toString returns user friendly representation" {
        REmpty shouldConvertToString "empty"
    }
})
