package com.mattmik.rapira.variables

import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.util.Result

/**
 * A container around instances of [RObject].
 *
 * Instances of [RObject] are considered immutable. [Variable] is used in
 * conjunction with [com.mattmik.rapira.Environment] to store and update Rapira
 * objects in memory.
 */
interface Variable {

    fun getValue(): Result<RObject>

    fun setValue(obj: RObject): Result<Unit>
}
