package com.mattmik.rapira.objects

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.create
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string

fun convertToString(expected: String) = object : Matcher<RObject> {
    override fun test(value: RObject) =
        MatcherResult(
            value.toString() == expected,
            "Object $value should convert to string $expected",
            "Object $value should not convert to string $expected"
        )
}

infix fun RObject.shouldConvertToString(expected: String) = this should convertToString(expected)

fun succeedWith(expected: RObject) = object : Matcher<OperationResult> {
    override fun test(value: OperationResult) =
        MatcherResult(
            value is OperationResult.Success && value.obj == expected,
            "OperationResult $value should be successful with value $expected",
            "OperationResult $value should not be successful with value $expected"
        )
}

infix fun OperationResult.shouldSucceedWith(expected: RObject) = this should succeedWith(expected)

val rapiraEmptyArb = Arb.create { Empty }
val rapiraFunctionArb = Arb.create { Function() }
val rapiraIntegerArb = Arb.int().map { num -> num.toRInteger() }
val rapiraLogicalArb = Arb.bool().map { bool -> Logical(bool) }
val rapiraProcedureArb = Arb.create { Procedure() }
val rapiraRealArb = Arb.double().map { double -> Real(double) }
val rapiraTextArb = Arb.string().map { str -> str.toText() }
val rapiraObjectArb = Arb.choice(
    rapiraEmptyArb,
    rapiraFunctionArb,
    rapiraIntegerArb,
    rapiraLogicalArb,
    rapiraProcedureArb,
    rapiraRealArb,
    rapiraTextArb
)
