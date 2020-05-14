package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.variables.SimpleVariable
import io.kotest.core.spec.style.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll

class BaseCallableTest : WordSpec({

    "call" should {
        "read extern objects from old environment" {
            val mockEnvironment = mockk<Environment>()
            val extern = listOf(
                "externVariable1",
                "externVariable2",
                "externVariable3"
            )
            every { mockEnvironment[any()] } returns SimpleVariable(Empty)
            val baseCallable = BaseCallable(
                statements = null,
                params = emptyList(),
                extern = extern
            )

            baseCallable.call(mockEnvironment, emptyList())

            verifyAll {
                mockEnvironment["externVariable1"]
                mockEnvironment["externVariable2"]
                mockEnvironment["externVariable3"]
            }
        }
    }
})
