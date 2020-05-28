package com.mattmik.rapira.control

class RepeatLoopController(private var counter: Int) : LoopController {

    override fun isLoopActive() =
        counter > 0

    override fun update() {
        counter--
    }
}
