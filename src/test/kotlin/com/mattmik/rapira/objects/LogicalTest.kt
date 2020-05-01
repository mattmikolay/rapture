package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class LogicalTest : WordSpec({
    "and" should {
        "return logical when given logical" {
            checkAll<Boolean, Boolean> { a, b ->
                Logical(a) and Logical(b) shouldBe Logical(a && b)
            }
        }

        "throw exception when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(RInteger(1)),
                row(Real(1.0)),
                row(Text("hello")),
                row(Sequence())
            ) { obj ->
                shouldThrow<RapiraInvalidOperationError> { Logical(true) and obj }
                shouldThrow<RapiraInvalidOperationError> { Logical(false) and obj }
            }
        }
    }

    "or" should {
        "return logical when given logical" {
            checkAll<Boolean, Boolean> { a, b ->
                Logical(a) or Logical(b) shouldBe Logical(a || b)
            }
        }

        "throw exception when given other types" {
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
    }

    "not" should {
        "return logical" {
            checkAll<Boolean> { a ->
                Logical(a).not() shouldBe Logical(!a)
            }
        }
    }

    "toString" should {
        "return user friendly representation" {
            Logical(true) shouldConvertToString "yes"
            Logical(false) shouldConvertToString "no"
        }
    }
})
