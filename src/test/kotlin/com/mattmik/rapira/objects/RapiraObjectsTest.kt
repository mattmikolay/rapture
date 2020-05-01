package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

fun assertEquals(expected: RObject, actual: RObject) {
    // TODO Figure out a better way to deal with double equality
    if (expected is Real && actual is Real) {
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
        Triple(Real(7.1), Real(3.8), Real(10.9)),
        Triple(Real(3.8), Real(7.1), Real(10.9)),
        Triple(Text("Hello, "), Text("world!"), Text("Hello, world!")),
        Triple(
            Sequence(emptyList()),
            Sequence(emptyList()),
            Sequence(emptyList())
        ),
        Triple(
            Sequence(listOf(RInteger(1))),
            Sequence(listOf(RInteger(2), RInteger((3)))),
            Sequence(listOf(RInteger(1), RInteger(2), RInteger((3))))
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
        Pair(Empty, RInteger(4)),
        Pair(RInteger(4), Empty),
        Pair(Empty, Real(4.1)),
        Pair(Real(4.1), Empty)
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
        Triple(Real(7.1), Real(3.8), Real(3.3)),
        Triple(Real(3.8), Real(7.1), Real(-3.3))
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
        Pair(Empty, RInteger(4)),
        Pair(RInteger(4), Empty),
        Pair(Empty, Real(4.1)),
        Pair(Real(4.1), Empty)
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
        Empty
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
        Triple(Real(7.1), Real(3.8), Real(26.98)),
        Triple(Real(3.8), Real(7.1), Real(26.98)),
        Triple(Text("hello"), RInteger(3), Text("hellohellohello")),
        Triple(RInteger(3), Text("hello"), Text("hellohellohello")),
        Triple(
            RInteger(3),
            Sequence(listOf(Text("hello"), Text("world"))),
            Sequence(
                listOf(
                    Text("hello"), Text("world"),
                    Text("hello"), Text("world"),
                    Text("hello"), Text("world")
                )
            )
        ),
        Triple(
            Sequence(listOf(Text("hello"), Text("world"))),
            RInteger(3),
            Sequence(
                listOf(
                    Text("hello"), Text("world"),
                    Text("hello"), Text("world"),
                    Text("hello"), Text("world")
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
        Pair(Empty, RInteger(4)),
        Pair(RInteger(4), Empty),
        Pair(Empty, Real(4.1)),
        Pair(Real(4.1), Empty)
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
        Triple(RInteger(7), RInteger(2), Real(3.5)),
        Triple(Real(6.0), Real(3.0), Real(2.0)),
        Triple(Real(6.0), RInteger(3), Real(2.0)),
        Triple(Real(3.0), Real(6.0), Real(0.5)),
        Triple(Real(3.0), RInteger(6), Real(0.5))
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
        Pair(Empty, RInteger(4)),
        Pair(RInteger(4), Empty),
        Pair(Empty, Real(4.1)),
        Pair(Real(4.1), Empty)
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
        Pair(Empty, RInteger(4)),
        Pair(RInteger(4), Empty),
        Pair(Empty, Real(4.1)),
        Pair(Real(4.1), Empty),
        Pair(Real(4.1), RInteger(2)),
        Pair(RInteger(2), Real(4.1)),
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
        Pair(Empty, RInteger(4)),
        Pair(RInteger(4), Empty),
        Pair(Empty, Real(4.1)),
        Pair(Real(4.1), Empty)
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
        Triple(RInteger(4), Real(2.1), Real(18.3791736)),
        Triple(Real(7.0), Real(3.0), Real(343.0)),
        Triple(Real(7.0), RInteger(3), Real(343.0))
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
        Pair(Empty, RInteger(4)),
        Pair(RInteger(4), Empty),
        Pair(Empty, Real(4.1)),
        Pair(Real(4.1), Empty)
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
        Pair(Text("Hello, world!"), RInteger(13)),
        Pair(
            Sequence(
                listOf(
                    RInteger(1),
                    Text("hello"),
                    Sequence()
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
        Empty,
        Logical(true),
        Procedure(),
        Function(),
        RInteger(1),
        Real(1.0)
    ).map { value ->
        dynamicTest("#($value) throws error") {
            assertThrows<RapiraInvalidOperationError> { lengthOperation(value) }
        }
    }
}
