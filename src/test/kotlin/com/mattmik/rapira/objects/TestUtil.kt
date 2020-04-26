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

fun convertToString(expected: String) = object : Matcher<RapiraObject> {
    override fun test(value: RapiraObject) =
        MatcherResult(
            value.toString() == expected,
            "Object $value should convert to string $expected",
            "Object $value should not convert to string $expected"
        )
}

infix fun RapiraObject.shouldConvertToString(expected: String) = this should convertToString(expected)

val rapiraEmptyArb = Arb.create { RapiraEmpty }
val rapiraFunctionArb = Arb.create { RapiraFunction() }
val rapiraIntegerArb = Arb.int().map { num -> num.toRapiraInteger() }
val rapiraLogicalArb = Arb.bool().map { bool -> RapiraLogical(bool) }
val rapiraProcedureArb = Arb.create { RapiraProcedure }
val rapiraRealArb = Arb.double().map { double -> RapiraReal(double) }
val rapiraTextArb = Arb.string().map { str -> str.toRapiraText() }
val rapiraObjectArb = Arb.choice(
    rapiraEmptyArb,
    rapiraFunctionArb,
    rapiraIntegerArb,
    rapiraLogicalArb,
    rapiraProcedureArb,
    rapiraRealArb,
    rapiraTextArb
)
