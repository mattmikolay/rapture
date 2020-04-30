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

val rapiraEmptyArb = Arb.create { REmpty }
val rapiraFunctionArb = Arb.create { RFunction() }
val rapiraIntegerArb = Arb.int().map { num -> num.toRInteger() }
val rapiraLogicalArb = Arb.bool().map { bool -> RLogical(bool) }
val rapiraProcedureArb = Arb.create { RProcedure() }
val rapiraRealArb = Arb.double().map { double -> RReal(double) }
val rapiraTextArb = Arb.string().map { str -> str.toRText() }
val rapiraObjectArb = Arb.choice(
    rapiraEmptyArb,
    rapiraFunctionArb,
    rapiraIntegerArb,
    rapiraLogicalArb,
    rapiraProcedureArb,
    rapiraRealArb,
    rapiraTextArb
)
