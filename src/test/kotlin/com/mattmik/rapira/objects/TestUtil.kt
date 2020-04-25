package com.mattmik.rapira.objects

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

fun convertToString(expected: String) = object : Matcher<RapiraObject> {
    override fun test(value: RapiraObject) =
        MatcherResult(
            value.toString() == expected,
            "Object $value should convert to string $expected",
            "Object $value should not convert to string $expected"
        )
}

infix fun RapiraObject.shouldConvertToString(expected: String) = this should convertToString(expected)
