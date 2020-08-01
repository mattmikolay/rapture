package com.mattmik.rapira.control

import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.shouldSucceedWith
import com.mattmik.rapira.objects.toRInteger
import com.mattmik.rapira.util.Result
import com.mattmik.rapira.variables.SimpleVariable
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

        "initialize variable to 1 by default" {
            every { mockVariable.setValue(any()) } returns Result.Success(Unit)

            ForLoopController(mockVariable, fromValue = null, toValue = null, stepValue = null, ctx = mockContext)

            verify(exactly = 1) {
                mockVariable.setValue(RInteger(1))
            }
        }
    }

    // TODO Test isLoopActive

    "update" should {
        "increment variable with a default step of 1" {
            val initialInt = 123
            val simpleVariable = SimpleVariable(Empty)
            val fromValue = RInteger(initialInt)

            val loopController = ForLoopController(
                simpleVariable,
                fromValue,
                toValue = null,
                stepValue = null,
                ctx = mockContext
            )
            simpleVariable.getValue() shouldSucceedWith RInteger(initialInt)

            loopController.update()
            simpleVariable.getValue() shouldSucceedWith RInteger(initialInt + 1)

            loopController.update()
            simpleVariable.getValue() shouldSucceedWith RInteger(initialInt + 2)

            loopController.update()
            simpleVariable.getValue() shouldSucceedWith RInteger(initialInt + 3)
        }

        "increment variable with a custom step value" {
            val initialInt = 123
            val stepInt = 5
            val simpleVariable = SimpleVariable(Empty)
            val fromValue = RInteger(initialInt)

            val loopController = ForLoopController(
                simpleVariable,
                fromValue,
                toValue = null,
                stepValue = stepInt.toRInteger(),
                ctx = mockContext
            )
            simpleVariable.getValue() shouldSucceedWith RInteger(initialInt)

            loopController.update()
            simpleVariable.getValue() shouldSucceedWith RInteger(initialInt + stepInt)

            loopController.update()
            simpleVariable.getValue() shouldSucceedWith RInteger(initialInt + (2 * stepInt))

            loopController.update()
            simpleVariable.getValue() shouldSucceedWith RInteger(initialInt + (3 * stepInt))
        }
    }
})
