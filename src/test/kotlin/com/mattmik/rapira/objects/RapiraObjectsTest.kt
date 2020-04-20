package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class AdditionTest {

    private val addOperation = { a: RapiraObject, b: RapiraObject -> a.add(b) };

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RapiraInteger(7), RapiraInteger(3), RapiraInteger(10)),
        Triple(RapiraInteger(3), RapiraInteger(7), RapiraInteger(10))
    ).map { (first, second, expected) ->
        dynamicTest("$first + $second = $expected") {
            assertEquals(
                expected,
                addOperation(first, second)
            )
        }
    }

    @TestFactory
    fun invalidOperationsThrowError() = listOf(
        Pair(RapiraEmpty, RapiraInteger(4)),
        Pair(RapiraInteger(4), RapiraEmpty)
    ).map { (first, second) ->
        dynamicTest("$first + $second throws error") {
            assertThrows<RapiraInvalidOperationError> {
                addOperation(
                    first,
                    second
                )
            }
        }
    }
}

class SubtractionTest {

    private val subtractOperation = { a: RapiraObject, b: RapiraObject -> a.subtract(b) };

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RapiraInteger(7), RapiraInteger(3), RapiraInteger(4)),
        Triple(RapiraInteger(3), RapiraInteger(7), RapiraInteger(-4))
    ).map { (first, second, expected) ->
        dynamicTest("$first - $second = $expected") {
            assertEquals(
                expected,
                subtractOperation(first, second)
            )
        }
    }

    @TestFactory
    fun invalidOperationsThrowError() = listOf(
        Pair(RapiraEmpty, RapiraInteger(4)),
        Pair(RapiraInteger(4), RapiraEmpty)
    ).map { (first, second) ->
        dynamicTest("$first - $second throws error") {
            assertThrows<RapiraInvalidOperationError> {
                subtractOperation(
                    first,
                    second
                )
            }
        }
    }
}

class NegationTest {

    private val negateOperation = { a: RapiraObject -> a.negate() };

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Pair(RapiraInteger(7), RapiraInteger(-7)),
        Pair(RapiraInteger(-7), RapiraInteger(7))
    ).map { (value, expected) ->
        dynamicTest("-($value) = $expected") {
            assertEquals(
                expected,
                negateOperation(value)
            )
        }
    }

    @TestFactory
    fun invalidOperationsThrowError() = listOf(
        RapiraEmpty
    ).map { value ->
        dynamicTest("-($value) throws error") {
            assertThrows<RapiraInvalidOperationError> { negateOperation(value) }
        }
    }
}

class MultiplicationTest {

    private val multiplyOperation = { a: RapiraObject, b: RapiraObject -> a.multiply(b) };

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RapiraInteger(7), RapiraInteger(3), RapiraInteger(21)),
        Triple(RapiraInteger(3), RapiraInteger(7), RapiraInteger(21))
    ).map { (first, second, expected) ->
        dynamicTest("$first * $second = $expected") {
            assertEquals(
                expected,
                multiplyOperation(first, second)
            )
        }
    }

    @TestFactory
    fun invalidOperationsThrowError() = listOf(
        Pair(RapiraEmpty, RapiraInteger(4)),
        Pair(RapiraInteger(4), RapiraEmpty)
    ).map { (first, second) ->
        dynamicTest("$first * $second throws error") {
            assertThrows<RapiraInvalidOperationError> {
                multiplyOperation(
                    first,
                    second
                )
            }
        }
    }
}

class DivisionTest {

    private val divideOperation = { a: RapiraObject, b: RapiraObject -> a.divide(b) };

    @TestFactory
    @Disabled("Division not yet implemented")
    fun validOperationsReturnNewObject() = listOf(
        Triple(RapiraInteger(7), RapiraInteger(3), RapiraInteger(21)),
        Triple(RapiraInteger(3), RapiraInteger(7), RapiraInteger(21))
    ).map { (first, second, expected) ->
        dynamicTest("$first / $second = $expected") {
            assertEquals(
                expected,
                divideOperation(first, second)
            )
        }
    }

    @TestFactory
    fun invalidOperationsThrowError() = listOf(
        Pair(RapiraEmpty, RapiraInteger(4)),
        Pair(RapiraInteger(4), RapiraEmpty)
    ).map { (first, second) ->
        dynamicTest("$first / $second throws error") {
            assertThrows<RapiraInvalidOperationError> {
                divideOperation(
                    first,
                    second
                )
            }
        }
    }
}

class IntDivisionTest {

    private val intDivideOperation = { a: RapiraObject, b: RapiraObject -> a.intDivide(b) };

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RapiraInteger(7), RapiraInteger(3), RapiraInteger(2)),
        Triple(RapiraInteger(3), RapiraInteger(7), RapiraInteger(0))
    ).map { (first, second, expected) ->
        dynamicTest("$first // $second = $expected") {
            assertEquals(
                expected,
                intDivideOperation(first, second)
            )
        }
    }

    @TestFactory
    fun invalidOperationsThrowError() = listOf(
        Pair(RapiraEmpty, RapiraInteger(4)),
        Pair(RapiraInteger(4), RapiraEmpty)
    ).map { (first, second) ->
        dynamicTest("$first // $second throws error") {
            assertThrows<RapiraInvalidOperationError> {
                intDivideOperation(
                    first,
                    second
                )
            }
        }
    }
}

class ModulusTest {

    private val moduloOperation = { a: RapiraObject, b: RapiraObject -> a.modulus(b) };

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RapiraInteger(7), RapiraInteger(3), RapiraInteger(1)),
        Triple(RapiraInteger(3), RapiraInteger(7), RapiraInteger(3))
    ).map { (first, second, expected) ->
        dynamicTest("$first /% $second = $expected") {
            assertEquals(
                expected,
                moduloOperation(first, second)
            )
        }
    }

    @TestFactory
    fun invalidOperationsThrowError() = listOf(
        Pair(RapiraEmpty, RapiraInteger(4)),
        Pair(RapiraInteger(4), RapiraEmpty)
    ).map { (first, second) ->
        dynamicTest("$first /% $second throws error") {
            assertThrows<RapiraInvalidOperationError> {
                moduloOperation(
                    first,
                    second
                )
            }
        }
    }
}

class ExponentiationTest {

    private val powerOperation = { a: RapiraObject, b: RapiraObject -> a.power(b) };

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RapiraInteger(7), RapiraInteger(3), RapiraInteger(343)),
        Triple(RapiraInteger(3), RapiraInteger(7), RapiraInteger(2187))
    ).map { (first, second, expected) ->
        dynamicTest("$first ** $second = $expected") {
            assertEquals(
                expected,
                powerOperation(first, second)
            )
        }
    }

    @TestFactory
    fun invalidOperationsThrowError() = listOf(
        Pair(RapiraEmpty, RapiraInteger(4)),
        Pair(RapiraInteger(4), RapiraEmpty)
    ).map { (first, second) ->
        dynamicTest("$first ** $second throws error") {
            assertThrows<RapiraInvalidOperationError> {
                powerOperation(
                    first,
                    second
                )
            }
        }
    }
}
