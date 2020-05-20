package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row

private val testObject = object : RObject("test") {}

class RObjectTest : StringSpec({
    "binary operations throw exception" {
        val otherObject = Empty
        forAll(
            row { obj: RObject -> obj + otherObject },
            row { obj: RObject -> obj - otherObject },
            row { obj: RObject -> obj * otherObject },
            row { obj: RObject -> obj / otherObject },
            row { obj: RObject -> obj.intDivide(otherObject) },
            row { obj: RObject -> obj % otherObject },
            row { obj: RObject -> obj.elementAt(otherObject) }
        ) { operation ->
            shouldThrow<RapiraInvalidOperationError> { operation(testObject) }
        }
    }

    "unary operations throw exception" {
        forAll(
            row { obj: RObject -> obj.length() }
        ) { operation ->
            shouldThrow<RapiraInvalidOperationError> { operation(testObject) }
        }
    }

    "ternary operations throw exception" {
        val otherObject1 = Empty
        val otherObject2 = Empty
        forAll(
            row { obj: RObject -> obj.slice(otherObject1, otherObject2) }
        ) { operation ->
            shouldThrow<RapiraInvalidOperationError> { operation(testObject) }
        }
    }

    "compareTo throws exception" {
        val otherObject = Empty
        shouldThrow<RapiraInvalidOperationError> { testObject.compareTo(otherObject) }
    }
})
