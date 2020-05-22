package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row

private val testObject = object : RObject {}

class RObjectTest : StringSpec({
    "binary operations throw exception" {
        val otherObject = Empty
        forAll(
            row { obj: RObject -> obj + otherObject }
        ) { operation ->
            shouldThrow<RapiraInvalidOperationError> { operation(testObject) }
        }
    }

    "compareTo throws exception" {
        val otherObject = Empty
        shouldThrow<RapiraInvalidOperationError> { testObject.compareTo(otherObject) }
    }
})
