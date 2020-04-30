package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.errors.RapiraIncorrectArgumentCountError
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import kotlin.math.absoluteValue

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
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: REmpty
            if (arg !is RInteger && arg !is RReal) {
                throw RapiraIllegalArgumentException("Expected integer or real at argument 0")
            }
            return when (arg) {
                is RInteger -> arg.value.absoluteValue.toRInteger()
                is RReal -> arg.value.absoluteValue.toRReal()
                else -> throw RapiraInvalidOperationError("Cannot compute absolute value of given type")
            }
        }
    },
    "sign" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
    },
    "sqrt" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
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
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: REmpty
            return RLogical(arg is REmpty)
        }
    },
    "is_log" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: REmpty
            return RLogical(arg is RLogical)
        }
    },
    "is_int" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: REmpty
            return RLogical(arg is RInteger)
        }
    },
    "is_real" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: REmpty
            return RLogical(arg is RReal)
        }
    },
    "is_text" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: REmpty
            return RLogical(arg is RText)
        }
    },
    "is_seq" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: REmpty
            return RLogical(arg is RSequence)
        }
    },
    "is_proc" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: REmpty
            return RLogical(arg is RProcedure)
        }
    },
    "is_fun" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            val arg = arguments.getOrNull(0)?.evaluate(environment) ?: REmpty
            return RLogical(arg is RFunction || arg is NativeFunction)
        }
    },
    "sin" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
    },
    "cos" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
    },
    "tg" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
    },
    "arcsin" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
    },
    "arctg" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
    },
    "exp" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
    },
    "ln" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
    },
    "lg" to object : NativeFunction(1) {
        override fun callInternal(environment: Environment, arguments: List<Argument>): RObject? {
            TODO("Not yet implemented")
        }
    }
)
