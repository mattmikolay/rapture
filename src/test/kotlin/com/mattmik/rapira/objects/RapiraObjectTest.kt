package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row

private val testObject = object : RapiraObject("test") {}

class RapiraObjectTest : StringSpec({
    "binary operations throw exception" {
        val otherObject = RapiraEmpty
        forAll(
            row { obj: RapiraObject -> obj.add(otherObject) },
            row { obj: RapiraObject -> obj.subtract(otherObject) },
            row { obj: RapiraObject -> obj.multiply(otherObject) },
            row { obj: RapiraObject -> obj.divide(otherObject) },
            row { obj: RapiraObject -> obj.intDivide(otherObject) },
            row { obj: RapiraObject -> obj.modulus(otherObject) },
            row { obj: RapiraObject -> obj.power(otherObject) },
            row { obj: RapiraObject -> obj.lessThan(otherObject) },
            row { obj: RapiraObject -> obj.greaterThan(otherObject) },
            row { obj: RapiraObject -> obj.lessThanEqualTo(otherObject) },
            row { obj: RapiraObject -> obj.greaterThanEqualTo(otherObject) },
            row { obj: RapiraObject -> obj.and(otherObject) },
            row { obj: RapiraObject -> obj.or(otherObject) }
        ) { operation ->
            shouldThrow<RapiraInvalidOperationError> { operation(testObject) }
        }
    }

    "unary operations throw exception" {
        forAll(
            row { obj: RapiraObject -> obj.negate() },
            row { obj: RapiraObject -> obj.length() },
            row { obj: RapiraObject -> obj.not() }
        ) { operation ->
            shouldThrow<RapiraInvalidOperationError> { operation(testObject) }
        }
    }
})
