package com.mattmik.rapira.control

class MasterLoopController(private val controllers: List<LoopController>) : LoopController {

    override fun isLoopActive() =
        controllers.all { it.isLoopActive() }

    override fun update() =
        controllers.forEach { it.update() }
}
