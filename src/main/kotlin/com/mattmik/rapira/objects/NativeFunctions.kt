package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.errors.RapiraIncorrectArgumentCountError
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import kotlin.math.absoluteValue

private abstract class NativeFunction(
    val paramCount: Int
) : RapiraObject("native function"), RapiraCallable {

    override fun call(environment: Environment, arguments: List<Argument>): RapiraObject? {
        if (arguments.size != paramCount) {
            throw RapiraIncorrectArgumentCountError(paramCount, arguments.size)
        }
        return callInternal(environment, arguments)
    }

    abstract fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject?

    override fun toString() = "native function"
}

val nativeFunctions = mapOf<String, RapiraObject>(
    "abs" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: RapiraEmpty
            if (arg !is RapiraInteger && arg !is RapiraReal) {
                throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }
            return when (arg) {
                is RapiraInteger -> arg.value.absoluteValue.toRapiraInteger()
                is RapiraReal -> arg.value.absoluteValue.toRapiraReal()
                else -> throw RapiraInvalidOperationError("Cannot compute absolute value of given type")
            }
        }
    },
    "sign" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    },
    "sqrt" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    },
    "entier" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    },
    "round" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    },
    "rand" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    },
    "int_rand" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    },
    "index" to object : NativeFunction(2) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    },
    // TODO
    "is_empty" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraEmpty)
        }
    },
    "is_log" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraLogical)
        }
    },
    "is_int" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraInteger)
        }
    },
    "is_real" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraReal)
        }
    },
    "is_text" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraText)
        }
    },
    "is_seq" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraSequence)
        }
    },
    "is_proc" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraProcedure)
        }
    },
    "is_fun" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraFunction || arg is NativeFunction)
        }
    },
    "sin" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    },
    "cos" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    },
    "tg" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    },
    "arcsin" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    },
    "arctg" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    },
    "exp" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    },
    "ln" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    },
    "lg" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RapiraObject? {
            TODO("Not yet implemented")
        }
    }
)
