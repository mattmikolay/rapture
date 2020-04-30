package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

fun assertEquals(expected: RObject, actual: RObject) {
    // TODO Figure out a better way to deal with double equality
    if (expected is RReal && actual is RReal) {
        return Assertions.assertEquals(expected.value, actual.value, 0.00001)
    }
    return Assertions.assertEquals(expected, actual)
}

fun makeObjectOperationTests(
    operator: String,
    operation: (RObject, RObject) -> RObject,
    vararg testData: Triple<RObject, RObject, RObject>
) = testData.map { (first, second, expected) ->
    dynamicTest("$first $operator $second = $expected") {
        assertEquals(
            expected,
            operation(first, second)
        )
    }
}

class AdditionTest {

    private val addOperation = { a: RObject, b: RObject -> a + b }

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RInteger(7), RInteger(3), RInteger(10)),
        Triple(RInteger(3), RInteger(7), RInteger(10)),
        Triple(RReal(7.1), RReal(3.8), RReal(10.9)),
        Triple(RReal(3.8), RReal(7.1), RReal(10.9)),
        Triple(RText("Hello, "), RText("world!"), RText("Hello, world!")),
        Triple(
            RSequence(emptyList()),
            RSequence(emptyList()),
            RSequence(emptyList())
        ),
        Triple(
            RSequence(listOf(RInteger(1))),
            RSequence(listOf(RInteger(2), RInteger((3)))),
            RSequence(listOf(RInteger(1), RInteger(2), RInteger((3))))
        )
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
        Pair(REmpty, RInteger(4)),
        Pair(RInteger(4), REmpty),
        Pair(REmpty, RReal(4.1)),
        Pair(RReal(4.1), REmpty)
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

    private val subtractOperation = { a: RObject, b: RObject -> a - b }

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RInteger(7), RInteger(3), RInteger(4)),
        Triple(RInteger(3), RInteger(7), RInteger(-4)),
        Triple(RReal(7.1), RReal(3.8), RReal(3.3)),
        Triple(RReal(3.8), RReal(7.1), RReal(-3.3))
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
        Pair(REmpty, RInteger(4)),
        Pair(RInteger(4), REmpty),
        Pair(REmpty, RReal(4.1)),
        Pair(RReal(4.1), REmpty)
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

    private val negateOperation = { a: RObject -> a.negate() }

    @TestFactory
    fun invalidOperationsThrowError() = listOf(
        REmpty
    ).map { value ->
        dynamicTest("-($value) throws error") {
            assertThrows<RapiraInvalidOperationError> { negateOperation(value) }
        }
    }
}

class MultiplicationTest {

    private val multiplyOperation = { a: RObject, b: RObject -> a * b }

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RInteger(7), RInteger(3), RInteger(21)),
        Triple(RInteger(3), RInteger(7), RInteger(21)),
        Triple(RReal(7.1), RReal(3.8), RReal(26.98)),
        Triple(RReal(3.8), RReal(7.1), RReal(26.98)),
        Triple(RText("hello"), RInteger(3), RText("hellohellohello")),
        Triple(RInteger(3), RText("hello"), RText("hellohellohello")),
        Triple(
            RInteger(3),
            RSequence(listOf(RText("hello"), RText("world"))),
            RSequence(
                listOf(
                    RText("hello"), RText("world"),
                    RText("hello"), RText("world"),
                    RText("hello"), RText("world")
                )
            )
        ),
        Triple(
            RSequence(listOf(RText("hello"), RText("world"))),
            RInteger(3),
            RSequence(
                listOf(
                    RText("hello"), RText("world"),
                    RText("hello"), RText("world"),
                    RText("hello"), RText("world")
                )
            )
        )
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
        Pair(REmpty, RInteger(4)),
        Pair(RInteger(4), REmpty),
        Pair(REmpty, RReal(4.1)),
        Pair(RReal(4.1), REmpty)
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

    private val divideOperation = { a: RObject, b: RObject -> a / b }

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RInteger(6), RInteger(3), RInteger(2)),
        Triple(RInteger(7), RInteger(2), RReal(3.5)),
        Triple(RReal(6.0), RReal(3.0), RReal(2.0)),
        Triple(RReal(6.0), RInteger(3), RReal(2.0)),
        Triple(RReal(3.0), RReal(6.0), RReal(0.5)),
        Triple(RReal(3.0), RInteger(6), RReal(0.5))
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
        Pair(REmpty, RInteger(4)),
        Pair(RInteger(4), REmpty),
        Pair(REmpty, RReal(4.1)),
        Pair(RReal(4.1), REmpty)
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

    private val intDivideOperation = { a: RObject, b: RObject -> a.intDivide(b) }

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RInteger(7), RInteger(3), RInteger(2)),
        Triple(RInteger(3), RInteger(7), RInteger(0))
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
        Pair(REmpty, RInteger(4)),
        Pair(RInteger(4), REmpty),
        Pair(REmpty, RReal(4.1)),
        Pair(RReal(4.1), REmpty),
        Pair(RReal(4.1), RInteger(2)),
        Pair(RInteger(2), RReal(4.1)),
        Pair(RInteger(2), RInteger(0)),
        Pair(RInteger(2), RInteger(-1))
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

    private val moduloOperation = { a: RObject, b: RObject -> a % b }

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RInteger(7), RInteger(3), RInteger(1)),
        Triple(RInteger(3), RInteger(7), RInteger(3))
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
        Pair(REmpty, RInteger(4)),
        Pair(RInteger(4), REmpty),
        Pair(REmpty, RReal(4.1)),
        Pair(RReal(4.1), REmpty)
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

    private val powerOperation = { a: RObject, b: RObject -> a.power(b) }

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RInteger(7), RInteger(3), RInteger(343)),
        Triple(RInteger(3), RInteger(7), RInteger(2187)),
        Triple(RInteger(4), RReal(2.1), RReal(18.3791736)),
        Triple(RReal(7.0), RReal(3.0), RReal(343.0)),
        Triple(RReal(7.0), RInteger(3), RReal(343.0))
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
        Pair(REmpty, RInteger(4)),
        Pair(RInteger(4), REmpty),
        Pair(REmpty, RReal(4.1)),
        Pair(RReal(4.1), REmpty)
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

class LengthTest {

    private val lengthOperation = { a: RObject -> a.length() }

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Pair(RText("Hello, world!"), RInteger(13)),
        Pair(
            RSequence(
                listOf(
                    RInteger(1),
                    RText("hello"),
                    RSequence()
                )
            ),
            RInteger(3)
        )
    ).map { (value, expected) ->
        dynamicTest("#($value) = $expected") {
            assertEquals(
                expected,
                lengthOperation(value)
            )
        }
    }

    @TestFactory
    fun invalidOperationsThrowError() = listOf(
        REmpty,
        RLogical(true),
        RProcedure(),
        RFunction(),
        RInteger(1),
        RReal(1.0)
    ).map { value ->
        dynamicTest("#($value) throws error") {
            assertThrows<RapiraInvalidOperationError> { lengthOperation(value) }
        }
    }
}
