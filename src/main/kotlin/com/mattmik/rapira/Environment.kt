package com.mattmik.rapira

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.Logical
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.nativeFunctions
import com.mattmik.rapira.objects.toReal
import com.mattmik.rapira.objects.toText
import kotlin.math.PI

private val specialValues = nativeFunctions + mapOf(
    "empty" to Empty,
    "yes" to Logical(true),
    "no" to Logical(false),
    "lf" to System.lineSeparator().toText(),
    "pi" to PI.toReal()
)

class Environment {
    private val bindings = mutableMapOf<String, RObject>()

    operator fun set(name: String, value: RObject) {
        if (specialValues.containsKey(name)) {
            throw RapiraInvalidOperationError("Cannot overwrite reserved word $name")
        }
        bindings[name] = value
    }

    operator fun get(name: String) = specialValues[name] ?: bindings.getOrDefault(name, Empty)
}
