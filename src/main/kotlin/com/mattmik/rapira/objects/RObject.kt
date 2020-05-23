package com.mattmik.rapira.objects

import com.mattmik.rapira.util.Result

interface RObject {

    operator fun plus(other: RObject): Result<RObject> =
        Result.Error("Illegal addition operation")

    operator fun minus(other: RObject): Result<RObject> =
        Result.Error("Illegal subtraction operation")

    fun negate(): Result<RObject> =
        Result.Error("Illegal negation operation")

    operator fun times(other: RObject): Result<RObject> =
        Result.Error("Illegal multiplication operation")

    operator fun div(other: RObject): Result<RObject> =
        Result.Error("Illegal division operation")

    fun intDivide(other: RObject): Result<RObject> =
        Result.Error("Illegal integer division operation")

    operator fun rem(other: RObject): Result<RObject> =
        Result.Error("Illegal modulo operation")

    fun power(other: RObject): Result<RObject> =
        Result.Error("Illegal exponent operation")

    fun length(): Result<RObject> =
        Result.Error("Illegal length operation")

    infix fun and(other: RObject): Result<RObject> =
        Result.Error("Illegal and operation")

    infix fun or(other: RObject): Result<RObject> =
        Result.Error("Illegal or operation")

    fun not(): Result<RObject> =
        Result.Error("Illegal not operation")

    fun elementAt(other: RObject): Result<RObject> =
        Result.Error("Illegal indexing operation")

    fun slice(start: RObject? = null, end: RObject? = null): Result<RObject> =
        Result.Error("Illegal slice operation")

    fun compare(other: RObject): Result<Int> =
        Result.Error("Illegal comparison operation")
}
