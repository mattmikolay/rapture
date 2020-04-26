package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class RapiraLogicalTest : StringSpec({

    "and with logical returns logical" {
        checkAll<Boolean, Boolean> { a, b ->
            RapiraLogical(a) and RapiraLogical(b) shouldBe RapiraLogical(a && b)
        }
    }

    "and with other types throws exception" {
        val trueLogical = RapiraLogical(true)
        val falseLogical = RapiraLogical(false)
        forAll(
            row(RapiraEmpty),
            row(RapiraProcedure),
            row(RapiraFunction()),
            row(RapiraInteger(1)),
            row(RapiraReal(1.0)),
            row(RapiraText("hello")),
            row(RapiraSequence())
        ) { obj ->
            shouldThrow<RapiraInvalidOperationError> { trueLogical and obj }
            shouldThrow<RapiraInvalidOperationError> { falseLogical and obj }
        }
    }

    "or with logical returns logical" {
        checkAll<Boolean, Boolean> { a, b ->
            RapiraLogical(a) or RapiraLogical(b) shouldBe RapiraLogical(a || b)
        }
    }

    "or with other types throws exception" {
        val trueLogical = RapiraLogical(true)
        val falseLogical = RapiraLogical(false)
        forAll(
            row(RapiraEmpty),
            row(RapiraProcedure),
            row(RapiraFunction()),
            row(RapiraInteger(1)),
            row(RapiraReal(1.0)),
            row(RapiraText("hello")),
            row(RapiraSequence())
        ) { obj ->
            shouldThrow<RapiraInvalidOperationError> { trueLogical or obj }
            shouldThrow<RapiraInvalidOperationError> { falseLogical or obj }
        }
    }

    "not with logical returns logical" {
        checkAll<Boolean> { a ->
            RapiraLogical(a).not() shouldBe RapiraLogical(!a)
        }
    }

    "toString returns user friendly representation" {
        RapiraLogical(true) shouldConvertToString "yes"
        RapiraLogical(false) shouldConvertToString "no"
    }
})
