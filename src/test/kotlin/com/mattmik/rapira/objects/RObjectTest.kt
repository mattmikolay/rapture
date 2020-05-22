package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

private val testObject = object : RObject {}

class RObjectTest : StringSpec({
    "compareTo throws exception" {
        val otherObject = Empty
        shouldThrow<RapiraInvalidOperationError> { testObject.compareTo(otherObject) }
    }
})
