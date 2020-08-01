package com.mattmik.rapira.control

import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.util.Result
import com.mattmik.rapira.variables.Variable
import io.kotest.core.spec.style.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ForLoopControllerTest : WordSpec({
    val mockVariable: Variable = mockk()
    val mockContext: RapiraParser.ForClauseContext = mockk(relaxed = true)

    "init" should {
        "initialize variable using from expression" {
            val fromValue = RInteger(123)
            every { mockVariable.setValue(any()) } returns Result.Success(Unit)

            ForLoopController(mockVariable, fromValue, toValue = null, stepValue = null, ctx = mockContext)

            verify(exactly = 1) {
                mockVariable.setValue(fromValue)
            }
        }
    }
})
