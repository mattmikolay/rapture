package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import org.junit.jupiter.api.*
import org.junit.jupiter.api.DynamicTest.dynamicTest

fun assertEquals(expected: RapiraObject, actual: RapiraObject) {
    // TODO Figure out a better way to deal with double equality
    if (expected is RapiraReal && actual is RapiraReal) {
        return Assertions.assertEquals(expected.value, actual.value, 0.00001)
    }
    return Assertions.assertEquals(expected, actual)
}

class AdditionTest {

    private val addOperation = { a: RapiraObject, b: RapiraObject -> a.add(b) };

    @TestFactory
    fun validOperationsReturnNewObject() = listOf(
        Triple(RapiraInteger(7), RapiraInteger(3), RapiraInteger(10)),
        Triple(RapiraInteger(3), RapiraInteger(7), RapiraInteger(10)),
        Triple(RapiraReal(7.1), RapiraReal(3.8), RapiraReal(10.9)),
        Triple(RapiraReal(3.8), RapiraReal(7.1), RapiraReal(10.9)),
        Triple(RapiraText("Hello, "), RapiraText("world!"), RapiraText("Hello, world!")),
        Triple(
            RapiraSequence(emptyList()),
            RapiraSequence(emptyList()),
            RapiraSequence(emptyList())
        ),
        Triple(
            RapiraSequence(listOf(RapiraInteger(1))),
            RapiraSequence(listOf(RapiraInteger(2), RapiraInteger((3)))),
            RapiraSequence(listOf(RapiraInteger(1), RapiraInteger(2), RapiraInteger((3))))
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
        Pair(RapiraEmpty, RapiraInteger(4)),
        Pair(RapiraInteger(4), RapiraEmpty),
        Pair(RapiraEmpty, RapiraReal(4.1)),
        Pair(RapiraReal(4.1), RapiraEmpty)
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
        Triple(RapiraInteger(3), RapiraInteger(7), RapiraInteger(-4)),
        Triple(RapiraReal(7.1), RapiraReal(3.8), RapiraReal(3.3)),
        Triple(RapiraReal(3.8), RapiraReal(7.1), RapiraReal(-3.3))
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
        Pair(RapiraInteger(4), RapiraEmpty),
        Pair(RapiraEmpty, RapiraReal(4.1)),
        Pair(RapiraReal(4.1), RapiraEmpty)
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
        Pair(RapiraInteger(-7), RapiraInteger(7)),
        Pair(RapiraReal(3.8), RapiraReal(-3.8)),
        Pair(RapiraReal(-3.8), RapiraReal(3.8))
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
        Triple(RapiraInteger(3), RapiraInteger(7), RapiraInteger(21)),
        Triple(RapiraReal(7.1), RapiraReal(3.8), RapiraReal(26.98)),
        Triple(RapiraReal(3.8), RapiraReal(7.1), RapiraReal(26.98)),
        Triple(RapiraText("hello"), RapiraInteger(3), RapiraText("hellohellohello")),
        Triple(RapiraInteger(3), RapiraText("hello"), RapiraText("hellohellohello")),
        Triple(
            RapiraInteger(3),
            RapiraSequence(listOf(RapiraText("hello"), RapiraText("world"))),
            RapiraSequence(
                listOf(
                    RapiraText("hello"), RapiraText("world"),
                    RapiraText("hello"), RapiraText("world"),
                    RapiraText("hello"), RapiraText("world")
                )
            )
        ),
        Triple(
            RapiraSequence(listOf(RapiraText("hello"), RapiraText("world"))),
            RapiraInteger(3),
            RapiraSequence(
                listOf(
                    RapiraText("hello"), RapiraText("world"),
                    RapiraText("hello"), RapiraText("world"),
                    RapiraText("hello"), RapiraText("world")
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
        Pair(RapiraEmpty, RapiraInteger(4)),
        Pair(RapiraInteger(4), RapiraEmpty),
        Pair(RapiraEmpty, RapiraReal(4.1)),
        Pair(RapiraReal(4.1), RapiraEmpty)
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
    fun validOperationsReturnNewObject() = listOf(
        Triple(RapiraInteger(6), RapiraInteger(3), RapiraInteger(2)),
        Triple(RapiraInteger(7), RapiraInteger(2), RapiraReal(3.5)),
        Triple(RapiraReal(6.0), RapiraReal(3.0), RapiraReal(2.0)),
        Triple(RapiraReal(6.0), RapiraInteger(3), RapiraReal(2.0)),
        Triple(RapiraReal(3.0), RapiraReal(6.0), RapiraReal(0.5)),
        Triple(RapiraReal(3.0), RapiraInteger(6), RapiraReal(0.5))
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
        Pair(RapiraInteger(4), RapiraEmpty),
        Pair(RapiraEmpty, RapiraReal(4.1)),
        Pair(RapiraReal(4.1), RapiraEmpty)
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
        Pair(RapiraInteger(4), RapiraEmpty),
        Pair(RapiraEmpty, RapiraReal(4.1)),
        Pair(RapiraReal(4.1), RapiraEmpty),
        Pair(RapiraReal(4.1), RapiraInteger(2)),
        Pair(RapiraInteger(2), RapiraReal(4.1)),
        Pair(RapiraInteger(2), RapiraInteger(0)),
        Pair(RapiraInteger(2), RapiraInteger(-1))
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
        Pair(RapiraInteger(4), RapiraEmpty),
        Pair(RapiraEmpty, RapiraReal(4.1)),
        Pair(RapiraReal(4.1), RapiraEmpty)
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
        Triple(RapiraInteger(3), RapiraInteger(7), RapiraInteger(2187)),
        Triple(RapiraInteger(4), RapiraReal(2.1), RapiraReal(18.3791736)),
        Triple(RapiraReal(7.0), RapiraReal(3.0), RapiraReal(343.0)),
        Triple(RapiraReal(7.0), RapiraInteger(3), RapiraReal(343.0))
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
        Pair(RapiraInteger(4), RapiraEmpty),
        Pair(RapiraEmpty, RapiraReal(4.1)),
        Pair(RapiraReal(4.1), RapiraEmpty)
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

class StringRepresentationTest {

    @Test
    fun emptyToStringReturnsUserFriendlyRepresentation() = Assertions.assertEquals("empty", RapiraEmpty.toString())

    @Test
    fun logicalToStringReturnsUserFriendlyRepresentation() {
        Assertions.assertEquals("yes", RapiraLogical(true).toString())
        Assertions.assertEquals("no", RapiraLogical(false).toString())
    }

    @Test
    fun procedureToStringReturnsUserFriendlyRepresentation() =
        Assertions.assertEquals("procedure", RapiraProcedure.toString())

    @Test
    fun functionToStringReturnsUserFriendlyRepresentation() =
        Assertions.assertEquals("function", RapiraFunction.toString())

    @Test
    fun integerToStringReturnsUserFriendlyRepresentation() {
        Assertions.assertEquals("0", RapiraInteger(0).toString())
        Assertions.assertEquals("123", RapiraInteger(123).toString())
        Assertions.assertEquals("-123", RapiraInteger(-123).toString())
    }

    @Test
    fun realToStringReturnsUserFriendlyRepresentation() {
        Assertions.assertEquals("0.0", RapiraReal(0.0).toString())
        Assertions.assertEquals("1.23456789", RapiraReal(1.23456789).toString())
        Assertions.assertEquals("-1.23456789", RapiraReal(-1.23456789).toString())
    }

    @Test
    fun textToStringReturnsUserFriendlyRepresentation() {
        Assertions.assertEquals("\"Hello, world!\"", RapiraText("Hello, world!").toString())
        Assertions.assertEquals(
            "\"How about some \"double quotes\"? Fancy, eh?\"",
            RapiraText("How about some \"\"double quotes\"\"? Fancy, eh?").toString()
        )
    }

    @Test
    fun sequenceToStringReturnsUserFriendlyRepresentation() {
        Assertions.assertEquals("<* *>", RapiraSequence().toString())
        Assertions.assertEquals(
            "<* 1, 2, 3 *>",
            RapiraSequence(
                listOf(
                    RapiraInteger(1),
                    RapiraInteger(2),
                    RapiraInteger(3)
                )
            ).toString()
        )
        Assertions.assertEquals(
            "<* 1, <* 2, 3, 4 *>, 5 *>",
            RapiraSequence(
                listOf(
                    RapiraInteger(1),
                    RapiraSequence(
                        listOf(
                            RapiraInteger(2),
                            RapiraInteger(3),
                            RapiraInteger(4)
                        )
                    ),
                    RapiraInteger(5)
                )
            ).toString()
        )
        Assertions.assertEquals(
            "<* 1, 2.5, \"okay\" *>",
            RapiraSequence(
                listOf(
                    RapiraInteger(1),
                    RapiraReal(2.5),
                    RapiraText("okay")
                )
            ).toString()
        )
    }
}
