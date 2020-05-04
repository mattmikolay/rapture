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
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

private abstract class NativeFunction(
    val paramCount: Int
) : RObject("native function"), RapiraCallable {

    override fun call(environment: Environment, arguments: List<Argument>): RObject? {
        if (arguments.size != paramCount) {
            throw RapiraIncorrectArgumentCountError(paramCount, arguments.size)
        }
        return callInternal(environment, arguments)
    }

    abstract fun callInternal(environment: Environment, arguments: List<Argument>): RObject?

    override fun toString() = "native function"
}

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
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
    },
    "round" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
    },
    "rand" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
    },
    "int_rand" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
    },
    "index" to object : NativeFunction(2) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
    },
    // TODO
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
