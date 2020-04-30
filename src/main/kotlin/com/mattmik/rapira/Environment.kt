package com.mattmik.rapira

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.REmpty
import com.mattmik.rapira.objects.RLogical
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.nativeFunctions
import com.mattmik.rapira.objects.toRReal
import com.mattmik.rapira.objects.toRText
import kotlin.math.PI

private val specialValues = nativeFunctions + mapOf(
    "empty" to REmpty,
    "yes" to RLogical(true),
    "no" to RLogical(false),
    "lf" to System.lineSeparator().toRText(),
    "pi" to PI.toRReal()
)

class Environment {
    private val bindings = mutableMapOf<String, RObject>()

    operator fun set(name: String, value: RObject) {
        if (specialValues.containsKey(name)) {
            throw RapiraInvalidOperationError("Cannot overwrite reserved word $name")
        }
        bindings[name] = value
    }

    operator fun get(name: String) = specialValues[name] ?: bindings.getOrDefault(name, REmpty)
}
