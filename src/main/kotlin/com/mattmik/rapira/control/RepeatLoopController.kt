package com.mattmik.rapira.control

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.RObject

class RepeatLoopController(
    private var counter: Int
) : LoopController {

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

    override fun isLoopActive() =
        counter > 0

    override fun update() {
        counter--
    }
}
