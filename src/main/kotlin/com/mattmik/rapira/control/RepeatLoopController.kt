package com.mattmik.rapira.control

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject

class RepeatLoopController(
    private var counter: Int
) {
    val isLoopActive: Boolean
        get() = counter > 0

    constructor(
        counterObj: RObject
    ) : this(
        (counterObj as? RInteger)?.value ?: throw RapiraInvalidOperationError("Cannot call repeat with non-integer number")
    )

    init {
        if (counter < 0) {
            throw RapiraInvalidOperationError("Cannot call repeat negative integer")
        }
    }

    fun update() {
        counter--
    }
}
