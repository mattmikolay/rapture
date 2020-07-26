package com.mattmik.rapira.control

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class RepeatLoopControllerTest : WordSpec({

    "isLoopActive" should {
        "return true when counter is greater than 0" {
            RepeatLoopController(counter = 123)
                .isLoopActive()
                .shouldBeTrue()
        }

        "return false when counter is 0" {
            RepeatLoopController(counter = 0)
                .isLoopActive()
                .shouldBeFalse()
        }
    }

    "update" should {
        "decrement counter" {
            val initialCounter = 3
            val loopController = RepeatLoopController(initialCounter)

            for (i in initialCounter downTo 1) {
                loopController.isLoopActive().shouldBeTrue()
                loopController.update()
            }

            loopController.isLoopActive().shouldBeFalse()
        }
    }
})
