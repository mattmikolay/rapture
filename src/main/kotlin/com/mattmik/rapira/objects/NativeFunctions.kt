package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.errors.RapiraIncorrectArgumentCountError
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

private abstract class NativeFunction(
    val paramCount: Int
) : RObject, RCallable {

    override fun call(environment: Environment, arguments: List<Argument>) =
        if (arguments.size == paramCount)
            callInternal(environment, arguments)
        else throw RapiraIncorrectArgumentCountError(
            expectedArgCount = paramCount,
            actualArgCount = arguments.size
        )

    abstract fun callInternal(environment: Environment, arguments: List<Argument>): RObject?

    override fun toString() = "native function"
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
    "abs" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> arg.value.absoluteValue.toRInteger()
                is Real -> arg.value.absoluteValue.toReal()
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }
    },
    "sign" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> arg.value.sign
                is Real -> arg.value.sign.toInt()
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }.toRInteger()
    },
    "sqrt" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> sqrt(arg.value.toDouble())
                is Real -> sqrt(arg.value)
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }.toReal()
    },
    "entier" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> arg.value
                is Real -> floor(arg.value).toInt()
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }.toRInteger()
    },
    "round" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> arg.value
                is Real -> arg.value.let { if (it.isNaN()) 0 else round(it).toInt() }
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }.toRInteger()
    },
    "rand" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> Random.nextDouble() * arg.value
                is Real -> Random.nextDouble() * arg.value
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }.toReal()
    },
    "int_rand" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> (1..arg.value).random()
                is Real -> (1..arg.value.toInt()).random()
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }.toRInteger()
    },
    "index" to object : NativeFunction(2) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg1 = arguments[0].evaluate(environment).value
            return when (val arg2 = arguments[1].evaluate(environment).value) {
                is Sequence -> arg2.entries.indexOf(arg1) + 1
                is Text -> {
                    if (arg1 !is Text) {
                        throw RapiraIllegalArgumentException("Invalid type passed to index function")
                    }

                    arg2.value.indexOf(arg1.value) + 1
                }
                else -> throw RapiraIllegalArgumentException("Expected text or sequence at argument 1")
            }.toRInteger()
        }
    },
    "is_empty" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments[0].evaluate(environment).value
            return Logical(arg is Empty)
        }
    },
    "is_log" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments[0].evaluate(environment).value
            return Logical(arg is Logical)
        }
    },
    "is_int" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments[0].evaluate(environment).value
            return Logical(arg is RInteger)
        }
    },
    "is_real" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments[0].evaluate(environment).value
            return Logical(arg is Real)
        }
    },
    "is_text" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments[0].evaluate(environment).value
            return Logical(arg is Text)
        }
    },
    "is_seq" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments[0].evaluate(environment).value
            return Logical(arg is Sequence)
        }
    },
    "is_proc" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments[0].evaluate(environment).value
            return Logical(arg is Procedure)
        }
    },
    "is_fun" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments[0].evaluate(environment).value
            return Logical(arg is Function || arg is NativeFunction)
        }
    },
    "sin" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> sin(arg.value.toDouble())
                is Real -> sin(arg.value)
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }.toReal()
    },
    "cos" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> cos(arg.value.toDouble())
                is Real -> cos(arg.value)
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }.toReal()
    },
    "tg" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> tan(arg.value.toDouble())
                is Real -> tan(arg.value)
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }.toReal()
    },
    "arcsin" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> asin(arg.value.toDouble())
                is Real -> asin(arg.value)
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }.toReal()
    },
    "arctg" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> atan(arg.value.toDouble())
                is Real -> atan(arg.value)
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }.toReal()
    },
    "exp" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> exp(arg.value.toDouble())
                is Real -> exp(arg.value)
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }.toReal()
    },
    "ln" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> ln(arg.value.toDouble())
                is Real -> ln(arg.value)
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }.toReal()
    },
    "lg" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>) =
            when (val arg = arguments[0].evaluate(environment).value) {
                is RInteger -> log10(arg.value.toDouble())
                is Real -> log10(arg.value)
                else -> throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }.toReal()
    }
)
