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
            row { obj: RObject -> obj.power(otherObject) },
            row { obj: RObject -> obj lessThan otherObject },
            row { obj: RObject -> obj greaterThan otherObject },
            row { obj: RObject -> obj lessThanEqualTo otherObject },
            row { obj: RObject -> obj greaterThanEqualTo otherObject },
            row { obj: RObject -> obj and otherObject },
            row { obj: RObject -> obj or otherObject },
            row { obj: RObject -> obj.elementAt(otherObject) }
        ) { operation ->
            shouldThrow<RapiraInvalidOperationError> { operation(testObject) }
        }
    }

    "unary operations throw exception" {
        forAll(
            row { obj: RObject -> obj.negate() },
            row { obj: RObject -> obj.length() },
            row { obj: RObject -> obj.not() }
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
})
