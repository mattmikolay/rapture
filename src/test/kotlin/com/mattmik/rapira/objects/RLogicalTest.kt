package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class RLogicalTest : StringSpec({

    "and with logical returns logical" {
        checkAll<Boolean, Boolean> { a, b ->
            RLogical(a) and RLogical(b) shouldBe RLogical(a && b)
        }
    }

    "and with other types throws exception" {
        val trueLogical = RLogical(true)
        val falseLogical = RLogical(false)
        forAll(
            row(REmpty),
            row(RProcedure()),
            row(RFunction()),
            row(RInteger(1)),
            row(RReal(1.0)),
            row(RText("hello")),
            row(RSequence())
        ) { obj ->
            shouldThrow<RapiraInvalidOperationError> { trueLogical and obj }
            shouldThrow<RapiraInvalidOperationError> { falseLogical and obj }
        }
    }

    "or with logical returns logical" {
        checkAll<Boolean, Boolean> { a, b ->
            RLogical(a) or RLogical(b) shouldBe RLogical(a || b)
        }
    }

    "or with other types throws exception" {
        val trueLogical = RLogical(true)
        val falseLogical = RLogical(false)
        forAll(
            row(REmpty),
            row(RProcedure()),
            row(RFunction()),
            row(RInteger(1)),
            row(RReal(1.0)),
            row(RText("hello")),
            row(RSequence())
        ) { obj ->
            shouldThrow<RapiraInvalidOperationError> { trueLogical or obj }
            shouldThrow<RapiraInvalidOperationError> { falseLogical or obj }
        }
    }

    "not with logical returns logical" {
        checkAll<Boolean> { a ->
            RLogical(a).not() shouldBe RLogical(!a)
        }
    }

    "toString returns user friendly representation" {
        RLogical(true) shouldConvertToString "yes"
        RLogical(false) shouldConvertToString "no"
    }
})
