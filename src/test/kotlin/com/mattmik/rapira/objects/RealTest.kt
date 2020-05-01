package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class NewRealTest : StringSpec({
    "negate returns integer" {
        checkAll<Double> {
                num -> num.toReal().negate() shouldBe (-num).toReal()
        }
    }
})

class RealTest {

    private val trueLogical = Logical(true)
    private val falseLogical = Logical(false)

    @TestFactory
    fun lessThanWithNumberReturnsLogical() = makeObjectOperationTests(
        "<",
        { a: RObject, b: RObject -> a lessThan b },

        // Integer
        Triple(Real(0.0), RInteger(10), trueLogical),
        Triple(Real(10.0), RInteger(0), falseLogical),
        Triple(Real(10.0), RInteger(10), falseLogical),

        // Real
        Triple(Real(0.0), Real(10.0), trueLogical),
        Triple(Real(10.0), Real(0.0), falseLogical),
        Triple(Real(10.0), Real(10.0), falseLogical)
    )

    @Test
    fun lessThanWithOtherTypesThrowsException() = listOf(
        Empty,
        Procedure(),
        Function(),
        Text("hello"),
        Logical(true),
        Sequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { Real(10.0) lessThan it }
        assertThrows<RapiraInvalidOperationError> { Real(0.0) lessThan it }
        assertThrows<RapiraInvalidOperationError> { Real(-10.0) lessThan it }
    }

    @TestFactory
    fun greaterThanWithNumberReturnsLogical() = makeObjectOperationTests(
        ">",
        { a: RObject, b: RObject -> a greaterThan b },

        // Integer
        Triple(Real(0.0), RInteger(10), falseLogical),
        Triple(Real(10.0), RInteger(0), trueLogical),
        Triple(Real(10.0), RInteger(10), falseLogical),

        // Real
        Triple(Real(0.0), Real(10.0), falseLogical),
        Triple(Real(10.0), Real(0.0), trueLogical),
        Triple(Real(10.0), Real(10.0), falseLogical)
    )

    @Test
    fun greaterThanWithOtherTypesThrowsException() = listOf(
        Empty,
        Procedure(),
        Function(),
        Text("hello"),
        Logical(true),
        Sequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { Real(10.0) greaterThan it }
        assertThrows<RapiraInvalidOperationError> { Real(0.0) greaterThan it }
        assertThrows<RapiraInvalidOperationError> { Real(-10.0) greaterThan it }
    }

    @TestFactory
    fun lessThanEqualToWithNumberReturnsLogical() = makeObjectOperationTests(
        "<=",
        { a: RObject, b: RObject -> a lessThanEqualTo b },

        // Integer
        Triple(Real(0.0), RInteger(10), trueLogical),
        Triple(Real(10.0), RInteger(0), falseLogical),
        Triple(Real(10.0), RInteger(10), trueLogical),

        // Real
        Triple(Real(0.0), Real(10.0), trueLogical),
        Triple(Real(10.0), Real(0.0), falseLogical),
        Triple(Real(10.0), Real(10.0), trueLogical)
    )

    @Test
    fun lessThanEqualToWithOtherTypesThrowsException() = listOf(
        Empty,
        Procedure(),
        Function(),
        Text("hello"),
        Logical(true),
        Sequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { Real(10.0) lessThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { Real(0.0) lessThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { Real(-10.0) lessThanEqualTo it }
    }

    @TestFactory
    fun greaterThanEqualToWithNumberReturnsLogical() = makeObjectOperationTests(
        ">=",
        { a: RObject, b: RObject -> a greaterThanEqualTo b },

        // Integer
        Triple(Real(0.0), RInteger(10), falseLogical),
        Triple(Real(10.0), RInteger(0), trueLogical),
        Triple(Real(10.0), RInteger(10), trueLogical),

        // Real
        Triple(Real(0.0), Real(10.0), falseLogical),
        Triple(Real(10.0), Real(0.0), trueLogical),
        Triple(Real(10.0), Real(10.0), trueLogical)
    )

    @Test
    fun greaterThanEqualToWithOtherTypesThrowsException() = listOf(
        Empty,
        Procedure(),
        Function(),
        Text("hello"),
        Logical(true),
        Sequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { Real(10.0) greaterThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { Real(0.0) greaterThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { Real(-10.0) greaterThanEqualTo it }
    }

    @Test
    fun toStringReturnsUserFriendlyRepresentation() {
        Assertions.assertEquals("0.0", Real(0.0).toString())
        Assertions.assertEquals("1.23456789", Real(1.23456789).toString())
        Assertions.assertEquals("-1.23456789", Real(-1.23456789).toString())
    }
}
