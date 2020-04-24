package com.mattmik.rapira.objects

import io.kotest.core.spec.style.StringSpec

class RapiraProcedureTest : StringSpec({
    "toString returns user friendly representation" {
        RapiraProcedure shouldConvertToString "procedure"
    }
})
