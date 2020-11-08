package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.errors.IllegalArgumentError
import com.mattmik.rapira.errors.IncorrectArgumentCountError
import com.mattmik.rapira.errors.InvalidOperationError
import com.mattmik.rapira.util.getOrThrow
import org.antlr.v4.runtime.Token
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

private interface NativeFunction : RObject, Callable {

    override val typeName: String
        get() = "function"
}

private inline fun makeNativeFunction(
    name: String,
    crossinline functionDef: (
        environment: Environment,
        arguments: List<Argument>,
        callToken: Token
    ) -> RObject
) =
    name to object : NativeFunction {
        override fun call(environment: Environment, arguments: List<Argument>, callToken: Token): RObject? =
            functionDef(environment, arguments, callToken)

        override fun toString() =
            "fun[\"$name\"]"
    }

private inline fun makeNativeFunction(
    name: String,
    crossinline functionDef: (obj: RObject, arg: Argument) -> RObject,
) = makeNativeFunction(name) { environment, arguments, callToken ->
    if (arguments.size != 1) {
        throw IncorrectArgumentCountError(
            expectedArgCount = 1,
            actualArgCount = arguments.size,
            token = callToken,
        )
    }

    val arg = arguments[0]
    val obj = arg.evaluate(environment)
        .getValue()
        .getOrThrow { reason -> InvalidOperationError(reason, token = arg.token) }

    functionDef(obj, arg)
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
    makeNativeFunction("abs") { obj, arg ->
        when (obj) {
            is RInteger -> obj.value.absoluteValue.toRInteger()
            is Real -> obj.value.absoluteValue.toReal()
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }
    },
    makeNativeFunction("sign") { obj, arg ->
        when (obj) {
            is RInteger -> obj.value.sign
            is Real -> obj.value.sign.toInt()
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }.toRInteger()
    },
    makeNativeFunction("sqrt") { obj, arg ->
        when (obj) {
            is RInteger -> sqrt(obj.value.toDouble())
            is Real -> sqrt(obj.value)
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }.toReal()
    },
    makeNativeFunction("entier") { obj, arg ->
        when (obj) {
            is RInteger -> obj.value
            is Real -> floor(obj.value).toInt()
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }.toRInteger()
    },
    makeNativeFunction("round") { obj, arg ->
        when (obj) {
            is RInteger -> obj.value
            is Real -> obj.value.let { if (it.isNaN()) 0 else round(it).toInt() }
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }.toRInteger()
    },
    makeNativeFunction("rand") { obj, arg ->
        when (obj) {
            is RInteger -> Random.nextDouble() * obj.value
            is Real -> Random.nextDouble() * obj.value
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }.toReal()
    },
    makeNativeFunction("int_rand") { obj, arg ->
        when (obj) {
            is RInteger -> (1..obj.value).random()
            is Real -> (1..obj.value.toInt()).random()
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }.toRInteger()
    },
    makeNativeFunction("index") { environment, arguments, callToken ->
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

        when (arg2) {
            is Sequence -> arg2.entries.indexOf(arg1) + 1
            is Text -> {
                if (arg1 !is Text) {
                    throw IllegalArgumentError("Invalid type passed to index function", arguments[0])
                }

                arg2.value.indexOf(arg1.value) + 1
            }
            else -> throw IllegalArgumentError("Expected text or sequence at argument 1", arguments[1])
        }.toRInteger()
    },
    makeNativeFunction("is_empty") { obj, _ ->
        Logical(obj is Empty)
    },
    makeNativeFunction("is_log") { obj, _ ->
        Logical(obj is Logical)
    },
    makeNativeFunction("is_int") { obj, _ ->
        Logical(obj is RInteger)
    },
    makeNativeFunction("is_real") { obj, _ ->
        Logical(obj is Real)
    },
    makeNativeFunction("is_text") { obj, _ ->
        Logical(obj is Text)
    },
    makeNativeFunction("is_seq") { obj, _ ->
        Logical(obj is Sequence)
    },
    makeNativeFunction("is_proc") { obj, _ ->
        Logical(obj is Procedure)
    },
    makeNativeFunction("is_fun") { obj, _ ->
        Logical(obj is Function || obj is NativeFunction)
    },
    makeNativeFunction("sin") { obj, arg ->
        when (obj) {
            is RInteger -> sin(obj.value.toDouble())
            is Real -> sin(obj.value)
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }.toReal()
    },
    makeNativeFunction("cos") { obj, arg ->
        when (obj) {
            is RInteger -> cos(obj.value.toDouble())
            is Real -> cos(obj.value)
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }.toReal()
    },
    makeNativeFunction("tg") { obj, arg ->
        when (obj) {
            is RInteger -> tan(obj.value.toDouble())
            is Real -> tan(obj.value)
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }.toReal()
    },
    makeNativeFunction("arcsin") { obj, arg ->
        when (obj) {
            is RInteger -> asin(obj.value.toDouble())
            is Real -> asin(obj.value)
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }.toReal()
    },
    makeNativeFunction("arctg") { obj, arg ->
        when (obj) {
            is RInteger -> atan(obj.value.toDouble())
            is Real -> atan(obj.value)
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }.toReal()
    },
    makeNativeFunction("exp") { obj, arg ->
        when (obj) {
            is RInteger -> exp(obj.value.toDouble())
            is Real -> exp(obj.value)
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }.toReal()
    },
    makeNativeFunction("ln") { obj, arg ->
        when (obj) {
            is RInteger -> ln(obj.value.toDouble())
            is Real -> ln(obj.value)
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }.toReal()
    },
    makeNativeFunction("lg") { obj, arg ->
        when (obj) {
            is RInteger -> log10(obj.value.toDouble())
            is Real -> log10(obj.value)
            else -> throw IllegalArgumentError("Expected integer or real at argument 0", arg)
        }.toReal()
    }
)
