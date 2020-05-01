package com.mattmik.rapira.objects

import io.kotest.core.spec.style.StringSpec

class EmptyTest : StringSpec({
    "toString returns user friendly representation" {
        Empty shouldConvertToString "empty"
    }
})
