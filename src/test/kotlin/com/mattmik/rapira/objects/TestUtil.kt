package com.mattmik.rapira.objects

import com.mattmik.rapira.util.Result
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.types.beOfType
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.constant
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

fun <T> succeedWith(expected: T) = object : Matcher<Result<T>> {
    override fun test(value: Result<T>) =
        MatcherResult(
            value is Result.Success && value.obj == expected,
            "Result $value should be successful with value $expected",
            "Result $value should not be successful with value $expected"
        )
}

infix fun <T> Result<T>.shouldSucceedWith(expected: T) = this should succeedWith(expected)

fun <T> Result<T>.shouldError() = this should beOfType<Result.Error>()

fun <T> errorWith(expected: String) = object : Matcher<Result<T>> {
    override fun test(value: Result<T>) =
        MatcherResult(
            value is Result.Error && value.reason == expected,
            "Result $value should be error with reason $expected",
            "Result $value should not be error with reason $expected"
        )
}

infix fun <T> Result<T>.shouldErrorWith(reason: String) = this should errorWith(reason)

val rapiraEmptyArb = Arb.constant(Empty)
val rapiraFunctionArb = arbitrary { Function() }
val rapiraIntegerArb = Arb.int().map { num -> num.toRInteger() }
val rapiraLogicalArb = Arb.bool().map { bool -> Logical(bool) }
val rapiraProcedureArb = arbitrary { Procedure() }
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
