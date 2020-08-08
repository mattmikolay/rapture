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
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
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

    "isLoopActive" When {

        "to expression is absent" should {
            val fromInt = 10
            val toValue = null

            "return true" {
                every { mockVariable.setValue(any()) } returns Result.Success(Unit)

                ForLoopController(
                    mockVariable,
                    fromValue = fromInt.toRInteger(),
                    toValue = toValue,
                    stepValue = null,
                    ctx = mockContext
                ).isLoopActive().shouldBeTrue()
            }
        }

        "to expression is specified and step is unspecified" should {
            val stepValue = null

            "return true until variable exceeds positive to expression" {
                val fromInt = 10
                val toInt = 12
                val simpleVariable = SimpleVariable(Empty)

                val loopController = ForLoopController(
                    simpleVariable,
                    fromValue = fromInt.toRInteger(),
                    toValue = toInt.toRInteger(),
                    stepValue = stepValue,
                    ctx = mockContext
                )

                for (i in fromInt..toInt) {
                    loopController.isLoopActive().shouldBeTrue()
                    loopController.update()
                }

                loopController.isLoopActive().shouldBeFalse()
            }

            "return true until variable exceeds negative to expression" {
                val fromInt = -10
                val toInt = -8
                val simpleVariable = SimpleVariable(Empty)

                val loopController = ForLoopController(
                    simpleVariable,
                    fromValue = fromInt.toRInteger(),
                    toValue = toInt.toRInteger(),
                    stepValue = stepValue,
                    ctx = mockContext
                )

                for (i in fromInt..toInt) {
                    loopController.isLoopActive().shouldBeTrue()
                    loopController.update()
                }

                loopController.isLoopActive().shouldBeFalse()
            }
        }

        "to expression is specified and step is positive" should {
            val stepInt = 2

            "return true until variable exceeds positive to expression" {
                val fromInt = 10
                val toInt = 14
                val simpleVariable = SimpleVariable(Empty)

                val loopController = ForLoopController(
                    simpleVariable,
                    fromValue = fromInt.toRInteger(),
                    toValue = toInt.toRInteger(),
                    stepValue = stepInt.toRInteger(),
                    ctx = mockContext
                )

                for (i in fromInt..toInt step stepInt) {
                    loopController.isLoopActive().shouldBeTrue()
                    loopController.update()
                }

                loopController.isLoopActive().shouldBeFalse()
            }

            "return true until variable exceeds negative to expression" {
                val fromInt = -10
                val toInt = -6
                val simpleVariable = SimpleVariable(Empty)

                val loopController = ForLoopController(
                    simpleVariable,
                    fromValue = fromInt.toRInteger(),
                    toValue = toInt.toRInteger(),
                    stepValue = stepInt.toRInteger(),
                    ctx = mockContext
                )

                for (i in fromInt..toInt step stepInt) {
                    loopController.isLoopActive().shouldBeTrue()
                    loopController.update()
                }

                loopController.isLoopActive().shouldBeFalse()
            }
        }

        "to expression is specified and step is negative" should {
            val stepInt = -2

            "return true until variable exceeds positive to expression" {
                val fromInt = 18
                val toInt = 14
                val simpleVariable = SimpleVariable(Empty)

                val loopController = ForLoopController(
                    simpleVariable,
                    fromValue = fromInt.toRInteger(),
                    toValue = toInt.toRInteger(),
                    stepValue = stepInt.toRInteger(),
                    ctx = mockContext
                )

                for (i in fromInt downTo toInt step -stepInt) {
                    loopController.isLoopActive().shouldBeTrue()
                    loopController.update()
                }

                loopController.isLoopActive().shouldBeFalse()
            }

            "return true until variable exceeds negative to expression" {
                val fromInt = -10
                val toInt = -14
                val simpleVariable = SimpleVariable(Empty)

                val loopController = ForLoopController(
                    simpleVariable,
                    fromValue = fromInt.toRInteger(),
                    toValue = toInt.toRInteger(),
                    stepValue = stepInt.toRInteger(),
                    ctx = mockContext
                )

                for (i in fromInt downTo toInt step -stepInt) {
                    loopController.isLoopActive().shouldBeTrue()
                    loopController.update()
                }

                loopController.isLoopActive().shouldBeFalse()
            }
        }
    }

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
