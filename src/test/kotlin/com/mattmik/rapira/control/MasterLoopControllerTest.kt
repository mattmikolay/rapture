package com.mattmik.rapira.control

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class MasterLoopControllerTest : WordSpec({

    "isLoopActive" should {
        "return true when every controller in list is active" {
            val mockControllers = List<LoopController>(size = 3) {
                mockk { every { isLoopActive() } returns true }
            }

            MasterLoopController(mockControllers).isLoopActive() shouldBe true

            mockControllers.forEach {
                verify(exactly = 1) { it.isLoopActive() }
            }
        }

        "return false when at least one controller in list is inactive" {
            val mockControllers = listOf<LoopController>(
                mockk { every { isLoopActive() } returns true },
                mockk { every { isLoopActive() } returns true },
                mockk { every { isLoopActive() } returns false }
            )

            MasterLoopController(mockControllers).isLoopActive() shouldBe false

            mockControllers.forEach {
                verify(exactly = 1) { it.isLoopActive() }
            }
        }
    }

    "update" should {
        "update every controller in list" {
            val mockControllers = List(size = 3) {
                mockk<LoopController>(relaxed = true)
            }

            MasterLoopController(mockControllers).update()

            mockControllers.forEach {
                verify(exactly = 1) { it.update() }
            }
        }
    }
})
