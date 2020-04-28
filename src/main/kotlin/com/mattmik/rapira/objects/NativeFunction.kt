package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.errors.RapiraIllegalArgumentException
import com.mattmik.rapira.errors.RapiraInvalidOperationError
import kotlin.math.absoluteValue

private abstract class NativeFunction : RapiraObject("native function"), RapiraCallable {
    override fun toString() = "native function"
}

val nativeFunctions = mapOf<String, RapiraObject>(
    "abs" to object : NativeFunction() {
        override fun call(environment: Environment, arguments: List<RapiraObject>): RapiraObject? {
            val arg = arguments.getOrNull(0) ?: RapiraEmpty
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
    // TODO
    "is_empty" to object : NativeFunction() {
        override fun call(environment: Environment, arguments: List<RapiraObject>): RapiraObject? {
            val arg = arguments.getOrNull(0) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraEmpty)
        }
    },
    "is_log" to object : NativeFunction() {
        override fun call(environment: Environment, arguments: List<RapiraObject>): RapiraObject? {
            val arg = arguments.getOrNull(0) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraLogical)
        }
    },
    "is_int" to object : NativeFunction() {
        override fun call(environment: Environment, arguments: List<RapiraObject>): RapiraObject? {
            val arg = arguments.getOrNull(0) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraInteger)
        }
    },
    "is_real" to object : NativeFunction() {
        override fun call(environment: Environment, arguments: List<RapiraObject>): RapiraObject? {
            val arg = arguments.getOrNull(0) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraReal)
        }
    },
    "is_text" to object : NativeFunction() {
        override fun call(environment: Environment, arguments: List<RapiraObject>): RapiraObject? {
            val arg = arguments.getOrNull(0) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraText)
        }
    },
    "is_seq" to object : NativeFunction() {
        override fun call(environment: Environment, arguments: List<RapiraObject>): RapiraObject? {
            val arg = arguments.getOrNull(0) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraSequence)
        }
    },
    "is_proc" to object : NativeFunction() {
        override fun call(environment: Environment, arguments: List<RapiraObject>): RapiraObject? {
            val arg = arguments.getOrNull(0) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraProcedure)
        }
    },
    "is_fun" to object : NativeFunction() {
        override fun call(environment: Environment, arguments: List<RapiraObject>): RapiraObject? {
            val arg = arguments.getOrNull(0) ?: RapiraEmpty
            return RapiraLogical(arg is RapiraFunction || arg is NativeFunction)
        }
    }
)
