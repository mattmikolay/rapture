package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.errors.IllegalArgumentError
import com.mattmik.rapira.errors.IncorrectArgumentCountError
import com.mattmik.rapira.errors.InvalidOperationError
import com.mattmik.rapira.util.getOrThrow
import kotlin.math.absoluteValue
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.round
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.random.Random
import org.antlr.v4.runtime.Token

private interface NativeFunction : RObject, Callable

private interface SingleParamNativeFunction : RObject, Callable {

    override fun call(
        environment: Environment,
        arguments: List<Argument>,
        callToken: Token
    ): RObject? {
        if (arguments.size != 1) {
            throw IncorrectArgumentCountError(
                expectedArgCount = 1,
                actualArgCount = arguments.size,
                token = callToken
            )
        }

        val arg = arguments[0]
        val obj = arg.evaluate(environment)
            .getValue()
            .getOrThrow { reason -> InvalidOperationError(reason, token = arg.token) }

        return call(obj, arg)
    }

    fun call(obj: RObject, arg: Argument): RObject
}

/**
 * Defines native functions. The following "graphic procedures" are not
 * implemented:
 * - dot
 * - line
 * - cfer
 * - color
 * - region
 * - rect
 * - triangle
 * - circle
 */
val nativeFunctions = mapOf<String, RObject>(
    "abs" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> obj.value.absoluteValue.toRInteger()
                is Real -> obj.value.absoluteValue.toReal()
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }
    },
    "sign" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> obj.value.sign
                is Real -> obj.value.sign.toInt()
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }.toRInteger()
    },
    "sqrt" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> sqrt(obj.value.toDouble())
                is Real -> sqrt(obj.value)
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }.toReal()
    },
    "entier" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> obj.value
                is Real -> floor(obj.value).toInt()
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }.toRInteger()
    },
    "round" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> obj.value
                is Real -> obj.value.let { if (it.isNaN()) 0 else round(it).toInt() }
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }.toRInteger()
    },
    "rand" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> Random.nextDouble() * obj.value
                is Real -> Random.nextDouble() * obj.value
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }.toReal()
    },
    "int_rand" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> (1..obj.value).random()
                is Real -> (1..obj.value.toInt()).random()
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }.toRInteger()
    },
    "index" to object : NativeFunction {
        override fun call(
            environment: Environment,
            arguments: List<Argument>,
            callToken: Token
        ): RObject? {
            if (arguments.size != 2) {
                throw IncorrectArgumentCountError(
                    expectedArgCount = 2,
                    actualArgCount = arguments.size,
                    token = callToken
                )
            }

            val arg1 = arguments[0].evaluate(environment)
                .getValue()
                .getOrThrow { reason -> InvalidOperationError(reason, token = arguments[0].token) }
            val arg2 = arguments[1].evaluate(environment)
                .getValue()
                .getOrThrow { reason -> InvalidOperationError(reason, token = arguments[1].token) }

            return when (arg2) {
                is Sequence -> arg2.entries.indexOf(arg1) + 1
                is Text -> {
                    if (arg1 !is Text) {
                        throw IllegalArgumentError("Invalid type passed to index function", arguments[0])
                    }

                    arg2.value.indexOf(arg1.value) + 1
                }
                else -> throw IllegalArgumentError("Expected text or sequence at argument 1", arguments[1])
            }.toRInteger()
        }
    },
    "is_empty" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            Logical(obj is Empty)
    },
    "is_log" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            Logical(obj is Logical)
    },
    "is_int" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            Logical(obj is RInteger)
    },
    "is_real" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            Logical(obj is Real)
    },
    "is_text" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            Logical(obj is Text)
    },
    "is_seq" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            Logical(obj is Sequence)
    },
    "is_proc" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            Logical(obj is Procedure)
    },
    "is_fun" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            Logical(obj is Function || obj is NativeFunction)
    },
    "sin" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> sin(obj.value.toDouble())
                is Real -> sin(obj.value)
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }.toReal()
    },
    "cos" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> cos(obj.value.toDouble())
                is Real -> cos(obj.value)
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }.toReal()
    },
    "tg" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> tan(obj.value.toDouble())
                is Real -> tan(obj.value)
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }.toReal()
    },
    "arcsin" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> asin(obj.value.toDouble())
                is Real -> asin(obj.value)
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }.toReal()
    },
    "arctg" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> atan(obj.value.toDouble())
                is Real -> atan(obj.value)
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }.toReal()
    },
    "exp" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> exp(obj.value.toDouble())
                is Real -> exp(obj.value)
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }.toReal()
    },
    "ln" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> ln(obj.value.toDouble())
                is Real -> ln(obj.value)
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }.toReal()
    },
    "lg" to object : SingleParamNativeFunction {
        override fun call(obj: RObject, arg: Argument) =
            when (obj) {
                is RInteger -> log10(obj.value.toDouble())
                is Real -> log10(obj.value)
                else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
            }.toReal()
    }
)
