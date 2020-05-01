package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class LogicalTest : StringSpec({

    "and with logical returns logical" {
        checkAll<Boolean, Boolean> { a, b ->
            Logical(a) and Logical(b) shouldBe Logical(a && b)
        }
    }

    "and with other types throws exception" {
        val trueLogical = Logical(true)
        val falseLogical = Logical(false)
        forAll(
            row(Empty),
            row(Procedure()),
            row(Function()),
            row(RInteger(1)),
            row(Real(1.0)),
            row(Text("hello")),
            row(Sequence())
        ) { obj ->
            shouldThrow<RapiraInvalidOperationError> { trueLogical and obj }
            shouldThrow<RapiraInvalidOperationError> { falseLogical and obj }
        }
    }

    "or with logical returns logical" {
        checkAll<Boolean, Boolean> { a, b ->
            Logical(a) or Logical(b) shouldBe Logical(a || b)
        }
    }

    "or with other types throws exception" {
        val trueLogical = Logical(true)
        val falseLogical = Logical(false)
        forAll(
            row(Empty),
            row(Procedure()),
            row(Function()),
            row(RInteger(1)),
            row(Real(1.0)),
            row(Text("hello")),
            row(Sequence())
        ) { obj ->
            shouldThrow<RapiraInvalidOperationError> { trueLogical or obj }
            shouldThrow<RapiraInvalidOperationError> { falseLogical or obj }
        }
    }

    "not with logical returns logical" {
        checkAll<Boolean> { a ->
            Logical(a).not() shouldBe Logical(!a)
        }
    }

    "toString returns user friendly representation" {
        Logical(true) shouldConvertToString "yes"
        Logical(false) shouldConvertToString "no"
    }
})
