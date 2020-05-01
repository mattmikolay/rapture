package com.mattmik.rapira.objects

import io.kotest.core.spec.style.WordSpec

class EmptyTest : WordSpec({

    "toString" should {
        "return user friendly representation" {
            Empty shouldConvertToString "empty"
        }
    }
})
