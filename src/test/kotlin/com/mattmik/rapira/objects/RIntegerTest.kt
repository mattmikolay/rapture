package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NewRapiraIntegerTest : StringSpec({
    "addition with integer returns integer" {
        checkAll<Int, Int> { a, b ->
            a.toRInteger() + b.toRInteger() shouldBe (a + b).toRInteger()
        }
    }

    "addition with real returns real" {
        checkAll<Int, Double> { a, b ->
            a.toRInteger() + b.toReal() shouldBe (a + b).toReal()
        }
    }

    "subtraction with integer returns integer" {
        checkAll<Int, Int> { a, b ->
            a.toRInteger() - b.toRInteger() shouldBe (a - b).toRInteger()
        }
    }

    "subtraction with real returns real" {
        checkAll<Int, Double> { a, b ->
            a.toRInteger() - b.toReal() shouldBe (a - b).toReal()
        }
    }

    "less than with integer returns logical" {
        checkAll<Int, Int> { a, b ->
            a.toRInteger() lessThan b.toRInteger() shouldBe Logical(a < b)
        }
    }

    "less than with real returns logical" {
        checkAll<Int, Double> { a, b ->
            a.toRInteger() lessThan b.toReal() shouldBe Logical(a < b)
        }
    }

    "less than or equal to with integer returns logical" {
        checkAll<Int, Int> { a, b ->
            a.toRInteger() lessThanEqualTo b.toRInteger() shouldBe Logical(a <= b)
        }
    }

    "less than or equal to with real returns logical" {
        checkAll<Int, Double> { a, b ->
            a.toRInteger() lessThanEqualTo b.toReal() shouldBe Logical(a <= b)
        }
    }

    "greater than with integer returns logical" {
        checkAll<Int, Int> { a, b ->
            a.toRInteger() greaterThan b.toRInteger() shouldBe Logical(a > b)
        }
    }

    "greater than with real returns logical" {
        checkAll<Int, Double> { a, b ->
            a.toRInteger() greaterThan b.toReal() shouldBe Logical(a > b)
        }
    }

    "greater than or equal to with integer returns logical" {
        checkAll<Int, Int> { a, b ->
            a.toRInteger() greaterThanEqualTo b.toRInteger() shouldBe Logical(a >= b)
        }
    }

    "greater than or equal to with real returns logical" {
        checkAll<Int, Double> { a, b ->
            a.toRInteger() greaterThanEqualTo b.toReal() shouldBe Logical(a >= b)
        }
    }

    "negate returns integer" {
        checkAll<Int> {
            num -> num.toRInteger().negate() shouldBe (-num).toRInteger()
        }
    }

    // "modulus with integer returns integer" {
    //     forAll<Int, Int> { a, b ->
    //         RapiraInteger(a).modulus(b.toRapiraInteger()) == RapiraInteger(a % b)
    //     }
    // }

    "toString returns user friendly representation" {
        checkAll<Int> { num -> RInteger(num) shouldConvertToString "$num" }
    }
})

class RIntegerTest {

    @Test
    fun lessThanWithOtherTypesThrowsException() = listOf(
        Empty,
        Procedure(),
        Function(),
        Text("hello"),
        Logical(true),
        Sequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RInteger(10).lessThan(it) }
        assertThrows<RapiraInvalidOperationError> { RInteger(0).lessThan(it) }
        assertThrows<RapiraInvalidOperationError> { RInteger(-10).lessThan(it) }
    }

    @Test
    fun greaterThanWithOtherTypesThrowsException() = listOf(
        Empty,
        Procedure(),
        Function(),
        Text("hello"),
        Logical(true),
        Sequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RInteger(10) greaterThan it }
        assertThrows<RapiraInvalidOperationError> { RInteger(0) greaterThan it }
        assertThrows<RapiraInvalidOperationError> { RInteger(-10) greaterThan it }
    }

    @Test
    fun lessThanEqualToWithOtherTypesThrowsException() = listOf(
        Empty,
        Procedure(),
        Function(),
        Text("hello"),
        Logical(true),
        Sequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RInteger(10) lessThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RInteger(0) lessThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RInteger(-10) lessThanEqualTo it }
    }

    @Test
    fun greaterThanEqualToWithOtherTypesThrowsException() = listOf(
        Empty,
        Procedure(),
        Function(),
        Text("hello"),
        Logical(true),
        Sequence()
    ).forEach {
        assertThrows<RapiraInvalidOperationError> { RInteger(10) greaterThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RInteger(0) greaterThanEqualTo it }
        assertThrows<RapiraInvalidOperationError> { RInteger(-10) greaterThanEqualTo it }
    }
}
