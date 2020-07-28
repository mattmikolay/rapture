package com.mattmik.rapira.control

import com.mattmik.rapira.CONST_NO
import com.mattmik.rapira.CONST_YES
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.visitors.ExpressionVisitor
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class WhileLoopControllerTest : WordSpec({

    "isLoopActive" should {
        "return true until condition evaluates to no" {
            val mockCondition = mockk<RapiraParser.ExpressionContext>()
            val mockExpressionVisitor = mockk<ExpressionVisitor> {
                every { visit(mockCondition) } returnsMany listOf(
                    CONST_YES,
                    RInteger(123),
                    Text("Hello, world!"),
                    CONST_NO
                )
            }

            val loopController = WhileLoopController(mockCondition, mockExpressionVisitor)

            loopController.run {
                isLoopActive().shouldBeTrue()
                isLoopActive().shouldBeTrue()
                isLoopActive().shouldBeTrue()
                isLoopActive().shouldBeFalse()
            }

            verify(exactly = 4) {
                mockExpressionVisitor.visit(mockCondition)
            }
        }
    }
})
