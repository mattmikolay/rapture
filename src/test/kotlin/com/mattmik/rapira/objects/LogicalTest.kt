package com.mattmik.rapira.objects

import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.property.checkAll

class LogicalTest : WordSpec({
    "and" should {
        "succeed with logical when given logical" {
            checkAll<Boolean, Boolean> { a, b ->
                Logical(a) and Logical(b) shouldSucceedWith Logical(a && b)
            }
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(RInteger(1)),
                row(Real(1.0)),
                row(Text("hello")),
                row(Sequence())
            ) { obj ->
                (LogicalYes and obj).shouldError()
                (LogicalNo and obj).shouldError()
            }
        }
    }

    "or" should {
        "succeed with logical when given logical" {
            checkAll<Boolean, Boolean> { a, b ->
                Logical(a) or Logical(b) shouldSucceedWith Logical(a || b)
            }
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(RInteger(1)),
                row(Real(1.0)),
                row(Text("hello")),
                row(Sequence())
            ) { obj ->
                (LogicalYes or obj).shouldError()
                (LogicalNo or obj).shouldError()
            }
        }
    }

    "not" should {
        "succeed with logical" {
            checkAll<Boolean> { a ->
                Logical(a).not() shouldSucceedWith Logical(!a)
            }
        }
    }

    "toString" should {
        "return user friendly representation" {
            LogicalYes shouldConvertToString "yes"
            LogicalNo shouldConvertToString "no"
        }
    }
})
