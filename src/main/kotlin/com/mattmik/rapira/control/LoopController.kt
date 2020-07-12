package com.mattmik.rapira.control

/**
 * Controller used to manage the state of an individual loop.
 */
interface LoopController {

    /**
     * Returns `true` if the next iteration of the loop should be processed,
     * `false` otherwise.
     */
    fun isLoopActive(): Boolean

    /**
     * Updates this loop controller after completion of a single loop iteration.
     */
    fun update()
}
